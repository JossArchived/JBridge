package me.josscoder.jbridge.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import lombok.Data;

@Data
public abstract class DataPacket {

    private final byte pid;

    public abstract void encode(ByteArrayDataOutput output);
    public abstract void decode(ByteArrayDataInput input);

    public abstract void handle(DataPacket packet);
}
