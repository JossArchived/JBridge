package me.josscoder.jbridge.nukkit.lobby.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.josscoder.jbridge.nukkit.JBridgeNukkit;
import me.josscoder.jbridge.nukkit.lobby.JBridgeLobby;

public class LobbyCommand extends Command {

    public LobbyCommand() {
        super("lobby",
                "Return to lobby",
                "/lobby",
                new String[]{"hub"}
        );
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.isPlayer()) return false;
        Player player = (Player) sender;

        String balancedLobbyService = JBridgeLobby.getInstance().getBalancedLobbyServiceShortId();
        if (balancedLobbyService.isEmpty()) {
            player.sendMessage(TextFormat.RED + "There are no rotating lobby servers!");
            return false;
        }

        JBridgeNukkit.getInstance().transferPlayer(player, balancedLobbyService);
        return true;
    }
}
