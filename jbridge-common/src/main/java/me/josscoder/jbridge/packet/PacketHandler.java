package me.josscoder.jbridge.packet;

/**
 * Interface class which new PacketHandler classes should implement
 */
public interface PacketHandler {

    /**
     * Method called when encoding and sending the packet
     */
    void onSend(DataPacket packet);


    /**
     * Method called when decoding and receiving the packet
     */
    void onReceive(DataPacket packet);
}
