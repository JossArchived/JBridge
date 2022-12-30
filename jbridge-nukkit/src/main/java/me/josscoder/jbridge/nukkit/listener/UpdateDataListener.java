package me.josscoder.jbridge.nukkit.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.service.ServiceInfo;

public class UpdateDataListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onJoin(PlayerJoinEvent event) {
        ServiceInfo currentServiceInfo = JBridgeCore.getInstance().getCurrentServiceInfo();
        currentServiceInfo.addPlayer(event.getPlayer().getName());
        currentServiceInfo.pushUpdatePlayers();
    }

    private void removePlayer(String player) {
        ServiceInfo currentServiceInfo = JBridgeCore.getInstance().getCurrentServiceInfo();
        currentServiceInfo.removePlayer(player);
        currentServiceInfo.pushUpdatePlayers();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onQuit(PlayerQuitEvent event) {
        removePlayer(event.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onKick(PlayerKickEvent event) {
        removePlayer(event.getPlayer().getName());
    }
}
