package me.josscoder.jbridge.override;

import dev.waterdog.waterdogpe.event.defaults.PreTransferEvent;
import dev.waterdog.waterdogpe.event.defaults.ProxyPingEvent;
import dev.waterdog.waterdogpe.event.defaults.ProxyQueryEvent;
import dev.waterdog.waterdogpe.logger.Color;
import me.josscoder.jbridge.JBridgeCore;

public class GeneralListener {

    public static void onPing(ProxyPingEvent event) {
        event.setMaximumPlayerCount(JBridgeCore.getInstance().getServiceHandler().getMaxPlayers());
    }

    public static void onQuery(ProxyQueryEvent event) {
        event.setMaximumPlayerCount(JBridgeCore.getInstance().getServiceHandler().getMaxPlayers());
    }

    public static void onTransfer(PreTransferEvent event) {
        event.getPlayer().sendMessage(Color.GRAY + "Connecting you to " + event.getTargetServer().getServerName());
    }
}
