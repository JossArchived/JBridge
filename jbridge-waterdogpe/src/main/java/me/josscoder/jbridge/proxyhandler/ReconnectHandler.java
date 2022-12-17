package me.josscoder.jbridge.proxyhandler;

import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.logger.Color;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import dev.waterdog.waterdogpe.utils.types.IReconnectHandler;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.waterdogpe.JBridgeWaterdogPE;

public class ReconnectHandler implements IReconnectHandler {
    @Override
    public ServerInfo getFallbackServer(ProxiedPlayer player, ServerInfo oldServer, String kickMessage) {
        String proxyId = JBridgeWaterdogPE.getInstance().getServiceInfo().getGroupAndShortId();

        player.sendMessage("§8Unexpected? Report this §7(" + proxyId + "-" + oldServer.getServerName() + "): §c" + kickMessage +
                "\n" +
                Color.GREEN + "We will connect you to a hub shortly..."
        );

        String balancedHubService = JBridgeCore.getInstance().getServiceHandler().getBalancedHubServiceShortId();
        return ProxyServer.getInstance().getServerInfo(balancedHubService);
    }
}
