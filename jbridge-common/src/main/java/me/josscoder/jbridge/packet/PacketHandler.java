package me.josscoder.jbridge.packet;

public interface PacketHandler {

    void onSend(DataPacket packet);
    void onReceive(DataPacket packet);
}
