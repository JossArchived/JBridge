package me.josscoder.jbridge.waterdogpe.lobby.proxyhandler;

import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import dev.waterdog.waterdogpe.utils.types.IJoinHandler;
import me.josscoder.jbridge.waterdogpe.lobby.JBridgeLobby;

public class JoinHandler implements IJoinHandler {
    @Override
    public ServerInfo determineServer(ProxiedPlayer player) {
        String balancedLobbyService = JBridgeLobby.getInstance().getBalancedLobbyServiceShortId();
        return ProxyServer.getInstance().getServerInfo(balancedLobbyService);
    }
}
