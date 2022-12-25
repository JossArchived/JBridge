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

@Getter
public class JBridgeLobby extends Plugin {

    @Getter
    private static JBridgeLobby instance;

    private List<String> lobbyGroups;
    private ServiceHandler.SortMode sortMode;

    @Override
    public void onStartup() {
        instance = this;
    }

    @Override
    public void onEnable() {
        loadConfig();

        lobbyGroups = getConfig().getStringList("lobby-groups");
        sortMode = ServiceHandler.SortMode.valueOf(getConfig().getString("sort-mode"));

        getProxy().setJoinHandler(new JoinHandler());
        getProxy().setReconnectHandler(new ReconnectHandler());
    }

    public List<ServiceInfo> getLobbyServices() {
        List<ServiceInfo> services = new ArrayList<>();
        lobbyGroups.forEach(lobbyGroup -> services.addAll(
                JBridgeCore.getInstance().getServiceHandler().getGroupServices(lobbyGroup)
        ));

        return services;
    }

    public ServiceInfo getSortedLobbyService() {
        return JBridgeCore.getInstance()
                .getServiceHandler()
                .getSortedServiceFromList(getLobbyServices(), sortMode);
    }

    public String getSortedLobbyServiceShortId() {
        ServiceInfo serviceInfo = getSortedLobbyService();
        return serviceInfo == null ? "" : serviceInfo.getShortId();
    }
}
