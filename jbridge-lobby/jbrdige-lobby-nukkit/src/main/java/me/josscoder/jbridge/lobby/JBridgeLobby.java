package me.josscoder.jbridge.lobby;

import cn.nukkit.plugin.PluginBase;
import me.josscoder.jbridge.lobby.command.LobbyCommand;

public class JBridgeLobby extends PluginBase {

    @Override
    public void onEnable() {
        getServer().getCommandMap().register("lobby", new LobbyCommand());
    }
}
