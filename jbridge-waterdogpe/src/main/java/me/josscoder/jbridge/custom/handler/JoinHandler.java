package me.josscoder.jbridge.custom.handler;

import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import dev.waterdog.waterdogpe.utils.types.IJoinHandler;
import me.josscoder.jbridge.JBridgeCore;

public class JoinHandler implements IJoinHandler {
    @Override
    public ServerInfo determineServer(ProxiedPlayer player) {
        String balancedHubService = JBridgeCore.getInstance().getServiceHandler().getBalancedHubServiceId();
        return ProxyServer.getInstance().getServerInfo(balancedHubService);
    }
}
