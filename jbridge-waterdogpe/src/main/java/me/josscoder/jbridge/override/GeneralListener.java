package me.josscoder.jbridge.override;

import dev.waterdog.waterdogpe.event.defaults.PreTransferEvent;
import dev.waterdog.waterdogpe.event.defaults.ProxyPingEvent;
import dev.waterdog.waterdogpe.event.defaults.ProxyQueryEvent;
import me.josscoder.jbridge.JBridgeCore;

public class GeneralListener {

    public static void onPing(ProxyPingEvent event) {
        event.setMaximumPlayerCount(JBridgeCore.getInstance().getServiceHandler().getMaxPlayers());
    }

    public static void onQuery(ProxyQueryEvent event) {
        event.setMaximumPlayerCount(JBridgeCore.getInstance().getServiceHandler().getMaxPlayers());
    }

    public static void onTransfer(PreTransferEvent event) {
        event.getPlayer().sendMessage("§7Connecting you to §6" + event.getTargetServer().getServerName());
    }
}
