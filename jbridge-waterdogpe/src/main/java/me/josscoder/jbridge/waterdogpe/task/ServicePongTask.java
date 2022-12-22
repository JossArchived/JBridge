package me.josscoder.jbridge.waterdogpe.task;

import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.network.serverinfo.BedrockServerInfo;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.service.ServiceInfo;
import me.josscoder.jbridge.waterdogpe.JBridgeWaterdogPE;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.Map;

public class ServicePongTask implements Runnable {

    private final ProxyServer proxy = ProxyServer.getInstance();
    private final Logger logger = JBridgeWaterdogPE.getInstance().getLogger();

    @Override
    public void run() {
        Map<String, ServiceInfo> cacheServers = JBridgeCore.getInstance()
                .getServiceHandler()
                .getServiceInfoMapCache();

        proxy.getServers().forEach(bedrockServer -> {
            if (cacheServers.containsKey(bedrockServer.getServerName())) {
                ServiceInfo service = cacheServers.get(bedrockServer.getServerName());
                String address = bedrockServer.getAddress().getHostString() + ":" + bedrockServer.getAddress().getPort();

                if (!service.getAddress().equalsIgnoreCase(address)) {
                    bedrockServer.getPlayers().forEach(player ->
                            player.disconnect("Server IP changes while online, duplicate server?")
                    );
                    proxy.removeServerInfo(bedrockServer.getServerName());
                    registerService(service, AddType.UPDATE_ADDRESS);
                }
            } else {
                bedrockServer.getPlayers().forEach(proxiedPlayer ->
                        proxiedPlayer.disconnect("Server timed out, broken connection?")
                );
                proxy.removeServerInfo(bedrockServer.getServerName());
                logger.info(String.format("Removed %s due to timeout...", bedrockServer.getServerName()));
            }
        });

        cacheServers.values()
                .stream()
                .filter(service -> proxy.getServerInfo(service.getShortId()) == null)
                .forEach(service -> registerService(service, AddType.NORMAL));
    }

    private enum AddType {
        NORMAL,
        UPDATE_ADDRESS
    }

    private void registerService(ServiceInfo service, AddType addType) {
        String[] newAddress = service.getAddress().split(":");
        InetSocketAddress socketAddress = new InetSocketAddress(newAddress[0], Integer.parseInt(newAddress[1]));

        ServerInfo serverInfo = new BedrockServerInfo(service.getShortId(), socketAddress, socketAddress);
        boolean registered = proxy.registerServerInfo(serverInfo);

        if (registered) {
            switch (addType) {
                case NORMAL:
                    logger.info(String.format("Added %s (%s:%s)", service.getRegionGroupAndShortId(),
                            newAddress[0], newAddress[1])
                    );
                    break;
                case UPDATE_ADDRESS:
                    logger.warn(String.format("Server IP for \"%s\" updated!", service.getRegionGroupAndShortId()));
                    break;
            }
        } else {
            logger.warn(String.format("Could not add server %s because it already exists",
                    service.getRegionGroupAndShortId()
            ));
        }
    }
}
