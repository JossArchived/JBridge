package me.josscoder.jbridge.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.packet.base.ServiceCacheUpdatePacket;
import me.josscoder.jbridge.service.ServiceInfo;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PacketManager {

    @Getter
    private final Map<Byte, Class<? extends DataPacket>> registeredPackets = new HashMap<>();

    @Getter
    private final List<PacketHandler> packetHandlers = new ArrayList<>();

    public PacketManager() {
        subscribePacket(new ServiceCacheUpdatePacket());
        addPacketHandler(new PacketHandler() {
            @Override
            public void onSend(DataPacket packet) {}

            @Override
            public void onReceive(DataPacket packet) {
                if (!(packet instanceof ServiceCacheUpdatePacket)) return;

                JBridgeCore core = JBridgeCore.getInstance();
                ServiceInfo cache = core.getGson().fromJson(((ServiceCacheUpdatePacket) packet).cache, ServiceInfo.class);
                core.getServiceInfoCache().put(cache.getShortId(), cache);
            }
        });
    }

    public void subscribePacket(DataPacket ...packets) {
        Arrays.stream(packets).forEach(packet -> {
            JBridgeCore core = JBridgeCore.getInstance();

            if (!registeredPackets.containsKey(packet.getPid())) {
                registeredPackets.put(packet.getPid(), packet.getClass());

                if (core.isDebug()) {
                    core.getLogger().debug(String.format("DataPacket \"%s:%s\" has subscribed!",
                            packet.getPid(),
                            packet.getClass().getSimpleName()
                    ));
                }

                return;
            }

            core.getLogger().warn(String.format("DataPacket \"%s\" is already subscribed!",
                    packet.getPid()
            ));
        });
    }

    public void publishPacket(DataPacket ...packets) {
        Arrays.stream(packets).forEach(packet -> CompletableFuture.runAsync(() -> handlePacketEncoding(packet)));
    }

    public void handlePacketEncoding(DataPacket packet) {
        JBridgeCore core = JBridgeCore.getInstance();
        packet.setSender(core.getCurrentServiceInfo().getShortId());

        try (Jedis jedis = core.getJedisPool().getResource()) {
            ByteArrayDataOutput output = ByteStreams.newDataOutput();

            output.writeByte(packet.getPid());
            packet.mainEncode(output);

            jedis.publish(JBridgeCore.PACKET_CHANNEL, output.toByteArray());
            packetHandlers.forEach(packetHandler -> packetHandler.onSend(packet));

            if (!core.isDebug()) return;

            core.getLogger().debug(String.format("DataPacket \"%s:%s\" was encoded and sent!",
                    packet.getPid(),
                    packet.getClass().getSimpleName()
            ));
        }
    }

    public DataPacket getPacket(byte pid) {
        Class<? extends DataPacket> classInstance = registeredPackets.get(pid);
        if (classInstance == null) return null;

        try {
            return classInstance.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void handlePacketDecoding(byte[] message) {
        ByteArrayDataInput input = ByteStreams.newDataInput(message);
        byte pid = input.readByte();
        DataPacket packet = getPacket(pid);

        JBridgeCore core = JBridgeCore.getInstance();

        if (packet == null) {
            if (core.isDebug()) {
                core.getLogger().debug(String.format("DataPacket \"%s\" is not subscribed!", pid));
            }
            return;
        }

        packet.mainDecode(input);
        packetHandlers.forEach(packetHandler -> {
            Runnable runnable = () -> packetHandler.onReceive(packet);

            if (packet instanceof AsyncPacket) {
                CompletableFuture.runAsync(runnable);
            } else {
                runnable.run();
            }
        });

        if (!core.isDebug()) return;

        core.getLogger().debug(String.format("DataPacket \"%s:%s\" was decoded and handled!",
                packet.getPid(),
                packet.getClass().getSimpleName()
        ));
    }

    public void addPacketHandler(PacketHandler packetHandler) {
        packetHandlers.add(packetHandler);
    }
}
