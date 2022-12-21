package me.josscoder.jbridge.waterdogpe.lobby;

import dev.waterdog.waterdogpe.plugin.Plugin;
import lombok.Getter;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.service.ServiceHandler;
import me.josscoder.jbridge.service.ServiceInfo;
import me.josscoder.jbridge.waterdogpe.lobby.proxyhandler.JoinHandler;
import me.josscoder.jbridge.waterdogpe.lobby.proxyhandler.ReconnectHandler;

import java.util.ArrayList;
import java.util.List;

public class JBridgeLobby extends Plugin {

    @Getter
    private static JBridgeLobby instance;

    @Getter
    private List<String> lobbyIds;

    @Override
    public void onStartup() {
        instance = this;
    }

    @Override
    public void onEnable() {
        loadConfig();

        lobbyIds = getConfig().getStringList("lobby-ids");

        getProxy().setJoinHandler(new JoinHandler());
        getProxy().setReconnectHandler(new ReconnectHandler());
    }

    public List<ServiceInfo> getLobbyServices() {
        List<ServiceInfo> services = new ArrayList<>();
        lobbyIds.forEach(lobbyId ->
                services.addAll(JBridgeCore.getInstance().getServiceHandler().getGroupServices(lobbyId))
        );

        return services;
    }

    public ServiceInfo getBalancedLobbyService() {
        return JBridgeCore.getInstance()
                .getServiceHandler()
                .getSortedServiceFromList(getLobbyServices(), ServiceHandler.SortMode.LOWEST);
    }

    public String getBalancedLobbyServiceShortId() {
        ServiceInfo serviceInfo = getBalancedLobbyService();
        return serviceInfo == null ? "" : serviceInfo.getShortId();
    }
}
