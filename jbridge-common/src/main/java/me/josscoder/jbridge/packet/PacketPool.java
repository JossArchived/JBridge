package me.josscoder.jbridge.packet;

import lombok.Getter;

public class PacketPool {

    @Getter
    private static PacketPool instance;

    public PacketPool() {
        instance = this;
    }
}
