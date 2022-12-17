package me.josscoder.jbridge.packet.base;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import me.josscoder.jbridge.packet.DataPacket;

/**
 * Sample packet, used to keep jbridge clients and servers in sync
 */
public class ServiceCacheUpdatePacket extends DataPacket {

    public String cache;

    public ServiceCacheUpdatePacket() {
        super((byte) 0x01);
    }

    @Override
    public void encode(ByteArrayDataOutput output) {
        output.writeUTF(cache);
    }

    @Override
    public void decode(ByteArrayDataInput input) {
        cache = input.readUTF();
    }
}
