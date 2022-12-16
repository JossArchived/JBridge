package me.josscoder.jbridge.packet;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.service.ServiceInfo;

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

    @Override
    public void handle(DataPacket packet) {
        if (packet instanceof ServiceDataUpdatePacket) {
            JBridgeCore core = JBridgeCore.getInstance();
            ServiceInfo data = core.getGson().fromJson(((ServiceDataUpdatePacket) packet).data, ServiceInfo.class);
            core.getServiceInfoCache().put(data.getId(), data);
        }
    }
}
