package me.josscoder.jbridge.nukkit.lobby;

import cn.nukkit.plugin.PluginBase;
import lombok.Getter;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.nukkit.lobby.command.LobbyCommand;
import me.josscoder.jbridge.service.ServiceHandler;
import me.josscoder.jbridge.service.ServiceInfo;

import java.util.ArrayList;
import java.util.List;

public class JBridgeLobby extends PluginBase {

    @Getter
    private static JBridgeLobby instance;

    @Getter
    private List<String> lobbyIds;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        lobbyIds = getConfig().getStringList("lobby-ids");

        getServer().getCommandMap().register("lobby", new LobbyCommand());
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
                .getServiceFromListBySortMode(getLobbyServices(), ServiceHandler.SortMode.LOWEST);
    }

    public String getBalancedLobbyServiceShortId() {
        ServiceInfo serviceInfo = getBalancedLobbyService();
        return serviceInfo == null ? "" : serviceInfo.getShortId();
    }
}
