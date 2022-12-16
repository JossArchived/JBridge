package me.josscoder.jbridge.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.packet.base.ServiceDataUpdatePacket;
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
        subscribePacket(new ServiceDataUpdatePacket());
        addPacketHandler(new PacketHandler() {
            @Override
            public void onSend(DataPacket packet) {}

            @Override
            public void onReceive(DataPacket packet) {
                if (!(packet instanceof ServiceDataUpdatePacket)) return;

                JBridgeCore core = JBridgeCore.getInstance();
                ServiceInfo data = core.getGson().fromJson(((ServiceDataUpdatePacket) packet).data, ServiceInfo.class);
                core.getServiceInfoCache().put(data.getId(), data);
            }
        });
    }

    public void subscribePacket(DataPacket ...packets) {
        Arrays.stream(packets).forEach(packet -> {
            registeredPackets.putIfAbsent(packet.getPid(), packet.getClass());

            JBridgeCore core = JBridgeCore.getInstance();
            if (core.isDebug()) {
                core.getLogger().debug(String.format("DataPacket %s subscribed!", packet.getClass().getName()));
            }
        });
    }

    public void publishPacket(DataPacket ...packets) {
        Arrays.stream(packets).forEach(packet -> CompletableFuture.runAsync(() -> handlePacketEncoding(packet)));
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
                core.getLogger().debug(String.format("DataPacket %s is not subscribed!", pid));
            }
            return;
        }

        packet.decode(input);
        packetHandlers.forEach(packetHandler -> packetHandler.onReceive(packet));

        if (core.isDebug()) {
            core.getLogger().debug(String.format("DataPacket %s decoded and handled!", packet.getClass().getName()));
        }
    }

    public void handlePacketEncoding(DataPacket packet) {
        JBridgeCore core = JBridgeCore.getInstance();

        try (Jedis jedis = core.getJedisPool().getResource()) {
            ByteArrayDataOutput output = ByteStreams.newDataOutput();

            output.writeByte(packet.getPid());
            packet.encode(output);

            jedis.publish(JBridgeCore.PACKET_CHANNEL, output.toByteArray());
            packetHandlers.forEach(packetHandler -> packetHandler.onSend(packet));

            if (core.isDebug()) {
                core.getLogger().debug(String.format("DataPacket %s encoded and sent!", packet.getClass().getName()));
            }
        }
    }

    public void addPacketHandler(PacketHandler packetHandler) {
        packetHandlers.add(packetHandler);
    }
}
