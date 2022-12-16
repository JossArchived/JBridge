package me.josscoder.jbridge;

import dev.waterdog.waterdogpe.event.defaults.PreTransferEvent;
import dev.waterdog.waterdogpe.event.defaults.ProxyPingEvent;
import dev.waterdog.waterdogpe.event.defaults.ProxyQueryEvent;
import dev.waterdog.waterdogpe.network.serverinfo.BedrockServerInfo;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.plugin.Plugin;
import dev.waterdog.waterdogpe.utils.config.Configuration;
import lombok.Getter;
import me.josscoder.jbridge.service.ServiceInfo;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JBridgeWaterdogPE extends Plugin {

    @Getter
    private static JBridgeWaterdogPE instance;

    @Getter
    private ServiceInfo serviceInfo;

    @Override
    public void onStartup() {
        instance = this;
    }

    @Override
    public void onEnable() {
        loadConfig();

        Configuration config = getConfig();
        JBridgeCore.boot(config.getString("redis.hostname"),
                config.getInt("redis.port"),
                config.getString("redis.password"),
                config.getBoolean("debug"),
                new WaterdogPELogger()
        );

        serviceInfo = new ServiceInfo(
                config.getString("service.id"),
                "",
                config.getString("service.group", "proxy"),
                config.getString("service.region", "us"),
                config.getString("service.branch", "dev"),
                -1
        );

        subscribeEvents();
        handleServicesPing();
    }

    private void subscribeEvents() {
        getProxy().getEventManager().subscribe(ProxyPingEvent.class, JBridgeWaterdogPE::onPing);
        getProxy().getEventManager().subscribe(ProxyQueryEvent.class, JBridgeWaterdogPE::onQuery);
        getProxy().getEventManager().subscribe(PreTransferEvent.class, this::onTransfer);
    }

    private static void onPing(ProxyPingEvent event) {
        event.setMaximumPlayerCount(JBridgeCore.getMaxPlayers());
    }

    private static void onQuery(ProxyQueryEvent event) {
        event.setMaximumPlayerCount(JBridgeCore.getMaxPlayers());
    }

    public void onTransfer(PreTransferEvent event) {
        event.getPlayer().sendMessage("§7Connecting you to §6" + event.getTargetServer().getServerName());
    }

    private void handleServicesPing() {
        getProxy().getScheduler().scheduleRepeating(() -> {
            Map<String, ServiceInfo> backendServers = JBridgeCore.getServiceInfoCache();
            Collection<ServerInfo> currentServers = getProxy().getServers();

            Iterator<ServerInfo> iterator = currentServers.iterator();
            while (iterator.hasNext()) {
                ServerInfo serverInfo = iterator.next();

                if (backendServers.containsKey(serverInfo.getServerName())) {
                    ServiceInfo serviceInfo1 = backendServers.get(serverInfo.getServerName());
                    String address = serverInfo.getAddress().getHostName() + ":" + serverInfo.getAddress().getPort();

                    if (!serviceInfo1.getAddress().equalsIgnoreCase(address)) {
                        serverInfo.getPlayers().forEach(player -> player.disconnect("Server IP changes while online, duplicate server?"));

                        String[] newAddress = serviceInfo1.getAddress().split(":");

                        InetSocketAddress socketAddress = serviceInfo1.getBranch().startsWith("dev")
                                ? new InetSocketAddress("127.0.0.1", Integer.parseInt(newAddress[1]))
                                : new InetSocketAddress(newAddress[0], Integer.parseInt(newAddress[1]));

                        serverInfo = new BedrockServerInfo(serverInfo.getServerName(), socketAddress, socketAddress);

                        getProxy().removeServerInfo(serverInfo.getServerName());
                        if (getProxy().registerServerInfo(serverInfo)) {
                            getLogger().warn("Server IP for \"" + serverInfo.getServerName() + "\" updated!");
                        }
                    }
                } else {
                    serverInfo.getPlayers().forEach(proxiedPlayer ->
                            proxiedPlayer.disconnect("Server timed out, broken connection?")
                    );
                    iterator.remove();
                    getProxy().removeServerInfo(serverInfo.getServerName());
                    getLogger().info("Removed " + serverInfo.getServerName() + " due to timeout...");
                }
            }

            List<String> currentServersKeySet = currentServers.stream().map(ServerInfo::getServerName).toList();

            backendServers.entrySet().stream().filter(serverEntry -> !currentServersKeySet.contains(serverEntry.getKey())).forEach(serverEntry -> {
                ServiceInfo serviceInfo1 = backendServers.get(serverEntry.getKey());

                String[] newAddress = serviceInfo1.getAddress().split(":");

                InetSocketAddress socketAddress = serviceInfo1.getBranch().startsWith("dev")
                        ? new InetSocketAddress("127.0.0.1", Integer.parseInt(newAddress[1]))
                        : new InetSocketAddress(newAddress[0], Integer.parseInt(newAddress[1]));

                ServerInfo serverInfo = new BedrockServerInfo(serverEntry.getKey(), socketAddress, socketAddress);
                if (getProxy().registerServerInfo(serverInfo)) getLogger().info("Added " + serverEntry.getKey() + " (" + newAddress[0] + ":" + newAddress[1] + ")");
            });
        }, 20 * 5, true);
    }
}
