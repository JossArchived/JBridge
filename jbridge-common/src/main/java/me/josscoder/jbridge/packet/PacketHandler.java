package me.josscoder.jbridge.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.josscoder.jbridge.JBridgeCore;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PacketHandler {

    private final Map<Byte, Class<? extends DataPacket>> registeredPackets = new HashMap<>();

    public void subscribePacket(DataPacket ...packets) {
        Arrays.stream(packets).forEach(packet -> {
            registeredPackets.putIfAbsent(packet.getPid(), packet.getClass());

            JBridgeCore core = JBridgeCore.getInstance();
            if (core.isDebug()) core.getLogger().debug("DataPacket " + packet.getClass().getName() + " subscribed!");
        });
    }

    public void publishPacket(DataPacket ...packets) {
        Arrays.stream(packets).forEach(packet -> {
            CompletableFuture.runAsync(() -> handlePacketEncoding(packet));
        });
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
        packet.handle(packet);

        if (core.isDebug()) {
            core.getLogger().debug("DataPacket " + packet.getClass().getName() + " decoded and handled!");
        }
    }

    public void handlePacketEncoding(DataPacket packet) {
        JBridgeCore core = JBridgeCore.getInstance();

        try (Jedis jedis = core.getJedisPool().getResource()) {
            ByteArrayDataOutput output = ByteStreams.newDataOutput();

            output.writeByte(packet.getPid());
            packet.encode(output);

            jedis.publish(JBridgeCore.PACKET_CHANNEL, output.toByteArray());

            if (core.isDebug()) {
                core.getLogger().debug("Packet " + packet.getClass().getName() + " encoded and sent!");
            }
        }
    }
}
