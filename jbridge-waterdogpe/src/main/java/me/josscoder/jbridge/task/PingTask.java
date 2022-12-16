package me.josscoder.jbridge.task;

import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.network.serverinfo.BedrockServerInfo;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.JBridgeWaterdogPE;
import me.josscoder.jbridge.service.ServiceInfo;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PingTask implements Runnable {
    @Override
    public void run() {
        Map<String, ServiceInfo> backendServers = JBridgeCore.getInstance()
                .getServiceHandler()
                .getServiceInfoMapCache();

        ProxyServer proxy = ProxyServer.getInstance();
        Logger logger = JBridgeWaterdogPE.getInstance().getLogger();

        proxy.getServers().forEach(serverInfo -> {
            if (backendServers.containsKey(serverInfo.getServerName())) {
                ServiceInfo service = backendServers.get(serverInfo.getServerName());
                String address = serverInfo.getAddress().getHostName() + ":" + serverInfo.getAddress().getPort();

                if (!service.getAddress().equalsIgnoreCase(address)) {
                    serverInfo.getPlayers().forEach(player -> player.disconnect("Server IP changes while online, duplicate server?"));

                    String[] newAddress = service.getAddress().split(":");
                    InetSocketAddress socketAddress = new InetSocketAddress(newAddress[0], Integer.parseInt(newAddress[1]));

                    serverInfo = new BedrockServerInfo(serverInfo.getServerName(), socketAddress, socketAddress);

                    proxy.removeServerInfo(serverInfo.getServerName());
                    if (proxy.registerServerInfo(serverInfo)) {
                        logger.warn("Server IP for \"" + serverInfo.getServerName() + "\" updated!");
                    }
                }
            } else {
                serverInfo.getPlayers().forEach(proxiedPlayer ->
                        proxiedPlayer.disconnect("Server timed out, broken connection?")
                );
                proxy.removeServerInfo(serverInfo.getServerName());
                logger.info("Removed " + serverInfo.getServerName() + " due to timeout...");
            }
        });

        List<String> currentServersKeySet = proxy.getServers().stream()
                .map(ServerInfo::getServerName)
                .collect(Collectors.toList());

        backendServers.entrySet().stream().filter(serverEntry -> !currentServersKeySet.contains(serverEntry.getKey())).forEach(serverEntry -> {
            ServiceInfo service = backendServers.get(serverEntry.getKey());

            String[] newAddress = service.getAddress().split(":");
            InetSocketAddress socketAddress = new InetSocketAddress(newAddress[0], Integer.parseInt(newAddress[1]));

            ServerInfo serverInfo = new BedrockServerInfo(serverEntry.getKey(), socketAddress, socketAddress);
            if (proxy.registerServerInfo(serverInfo)) logger.info("Added " + serverEntry.getKey() + " (" + newAddress[0] + ":" + newAddress[1] + ")");
        });
    }
}
