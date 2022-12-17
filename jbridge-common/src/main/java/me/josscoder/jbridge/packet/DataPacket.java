package me.josscoder.jbridge.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import lombok.Data;

/**
 * Abstract class from which new packets should inherit their methods
 */
@Data
public abstract class DataPacket {

    /**
     * Packet identifier
     */
    private final byte pid;

    /**
     * Method to encode data using the output variable
     */
    public abstract void encode(ByteArrayDataOutput output);

    /**
     * Method to decode data using the input variable
     */
    public abstract void decode(ByteArrayDataInput input);
}
