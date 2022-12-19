package me.josscoder.jbridge.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import lombok.Data;
import me.josscoder.jbridge.JBridgeCore;

/**
 * Abstract class from which new packets should inherit their methods
 */
@Data
public abstract class DataPacket {

    /**
     * Packet identifier
     */
    private final byte pid;

    private String sender;

    public void mainEncode(ByteArrayDataOutput output) {
        output.writeUTF(sender);
        encode(output);
    }

    /**
     * Method to encode data using the output variable
     */
    public abstract void encode(ByteArrayDataOutput output);

    public void mainDecode(ByteArrayDataInput input) {
        sender = input.readUTF();
        decode(input);
    }

    /**
     * Method to decode data using the input variable
     */
    public abstract void decode(ByteArrayDataInput input);

    public boolean senderIsOneSelf() {
        return sender.equalsIgnoreCase(JBridgeCore.getInstance().getCurrentServiceInfo().getShortId());
    }
}
