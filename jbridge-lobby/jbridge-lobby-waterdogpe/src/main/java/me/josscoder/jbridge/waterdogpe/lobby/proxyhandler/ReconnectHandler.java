package me.josscoder.jbridge.waterdogpe.lobby.proxyhandler;

import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import dev.waterdog.waterdogpe.utils.types.IReconnectHandler;
import me.josscoder.jbridge.waterdogpe.lobby.JBridgeLobby;

public class ReconnectHandler implements IReconnectHandler {
    @Override
    public ServerInfo getFallbackServer(ProxiedPlayer player, ServerInfo oldServer, String kickMessage) {
        player.sendMessage(String.format("§8Unexpected? Report this §7(%s): §c%s" +
                "\n" +
                "§aWe will connect you to a lobby shortly...",
                oldServer.getServerName(),
                kickMessage
        ));

        String sortedLobbyService = JBridgeLobby.getInstance().getSortedLobbyServiceShortId();
        return ProxyServer.getInstance().getServerInfo(sortedLobbyService);
    }
}
