package me.josscoder.jbridge.task;

import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.JBridgeNukkit;
import me.josscoder.jbridge.packet.base.ServiceDataUpdatePacket;
import me.josscoder.jbridge.service.ServiceInfo;

public class ServicePingTask extends Task {
    @Override
    public void onRun(int i) {
        ServiceInfo serviceInfo = JBridgeNukkit.getInstance().getServiceInfo();

        serviceInfo.getPlayers().clear();
        Server.getInstance().getOnlinePlayers().values().forEach(player -> serviceInfo.addPlayer(player.getName()));

        JBridgeCore jBridgeCore = JBridgeCore.getInstance();
        jBridgeCore.getPacketManager().publishPacket(new ServiceDataUpdatePacket(){{
            data = jBridgeCore.getGson().toJson(serviceInfo);
        }});
    }
}
