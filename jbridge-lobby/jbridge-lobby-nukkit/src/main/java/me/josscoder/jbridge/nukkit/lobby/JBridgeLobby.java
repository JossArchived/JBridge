package me.josscoder.jbridge.nukkit.lobby;

import cn.nukkit.plugin.PluginBase;
import lombok.Getter;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.nukkit.lobby.command.LobbyCommand;
import me.josscoder.jbridge.service.ServiceHandler;
import me.josscoder.jbridge.service.ServiceInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class JBridgeLobby extends PluginBase {

    @Getter
    private static JBridgeLobby instance;

    private List<String> lobbyGroups;
    private ServiceHandler.SortMode sortMode;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        lobbyGroups = getConfig().getStringList("lobby-groups");
        sortMode = ServiceHandler.SortMode.valueOf(getConfig().getString("sort-mode",
                ServiceHandler.SortMode.LOWEST.name())
        );

        getServer().getCommandMap().register("lobby", new LobbyCommand());
    }

    public Set<ServiceInfo> getLobbyServices() {
        Set<ServiceInfo> services = new HashSet<>();
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

    public String geSortedLobbyServiceShortId() {
        ServiceInfo serviceInfo = getSortedLobbyService();
        return serviceInfo == null ? "" : serviceInfo.getShortId();
    }
}
