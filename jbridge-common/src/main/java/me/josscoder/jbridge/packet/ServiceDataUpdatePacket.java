package me.josscoder.jbridge.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class ServiceDataUpdatePacket extends DataPacket {

    public String data;

    public ServiceDataUpdatePacket() {
        super((byte) 0x01);
    }

    @Override
    public void encode(ByteArrayDataOutput output) {
        output.writeUTF(data);
    }

    @Override
    public void decode(ByteArrayDataInput input) {
        data = input.readUTF();
    }
}
