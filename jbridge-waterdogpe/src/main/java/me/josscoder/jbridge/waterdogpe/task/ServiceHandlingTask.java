package me.josscoder.jbridge.waterdogpe.task;

import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.network.serverinfo.BedrockServerInfo;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.service.ServiceHandler;
import me.josscoder.jbridge.service.ServiceInfo;
import me.josscoder.jbridge.waterdogpe.JBridgeWaterdogPE;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ServiceHandlingTask implements Runnable {

    private final ProxyServer proxy = ProxyServer.getInstance();
    private final Logger logger = JBridgeWaterdogPE.getInstance().getLogger();

    @Override
    public void run() {
        List<String> queueRemove = new ArrayList<>();

        ServiceHandler serviceHandler = JBridgeCore.getInstance().getServiceHandler();
        Set<ServiceInfo> cacheServers = serviceHandler.getServices();

        proxy.getServers().forEach(bedrockServer -> {
            if (serviceHandler.containsService(bedrockServer.getServerName())) {
                ServiceInfo service = serviceHandler.getService(bedrockServer.getServerName());
                String address = bedrockServer.getAddress().getHostString() + ":" + bedrockServer.getAddress().getPort();

                if (!service.getAddress().equalsIgnoreCase(address) ||
                        !service.getPublicAddress().equalsIgnoreCase(address)
                ) {
                    bedrockServer.getPlayers().forEach(player ->
                            player.disconnect("Server IP changes while online, duplicate server?")
                    );
                    proxy.removeServerInfo(bedrockServer.getServerName());
                    handleRegisterService(service, AddType.UPDATE_ADDRESS);
                    return;
                }

                if (bedrockServer.getPlayers().size() == 0) {
                    try {
                        bedrockServer.ping(5, TimeUnit.SECONDS).get();
                    } catch (InterruptedException | ExecutionException e) {
                        CompletableFuture.runAsync(() -> serviceHandler.removeService(bedrockServer.getServerName()));
                        queueRemove.add(bedrockServer.getServerName());
                    }
                }
            } else {
                bedrockServer.getPlayers().forEach(proxiedPlayer ->
                        proxiedPlayer.disconnect("Server timed out, broken connection?")
                );
                handleRemoveServer(bedrockServer.getServerName());
            }
        });

        queueRemove.forEach(this::handleRemoveServer);

        cacheServers
                .stream()
                .filter(service -> proxy.getServerInfo(service.getShortId()) == null)
                .forEach(service -> handleRegisterService(service, AddType.NORMAL));
    }

    private enum AddType {
        NORMAL,
        UPDATE_ADDRESS
    }

    private void handleRegisterService(ServiceInfo service, AddType addType) {
        String[] addressData = service.getAddress().split(":");
        InetSocketAddress address = new InetSocketAddress(addressData[0], Integer.parseInt(addressData[1]));

        String[] publicAddressData = service.getPublicAddress().split(":");
        InetSocketAddress publicAddress = new InetSocketAddress(publicAddressData[0], Integer.parseInt(publicAddressData[1]));

        ServerInfo serverInfo = new BedrockServerInfo(service.getShortId(), address, publicAddress);
        boolean registered = proxy.registerServerInfo(serverInfo);

        if (registered) {
            switch (addType) {
                case NORMAL:
                    logger.info(String.format("Added %s (%s:%s)",
                            service.getRegionGroupAndShortId(),
                            address,
                            publicAddress
                    ));
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

    private void handleRemoveServer(String serverName) {
        proxy.removeServerInfo(serverName);
        logger.info(String.format("Removed %s due to timeout...", serverName));
    }
}
