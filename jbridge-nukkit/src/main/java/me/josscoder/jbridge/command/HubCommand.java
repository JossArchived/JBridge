package me.josscoder.jbridge.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.JBridgeNukkit;

public class HubCommand extends Command {

    public HubCommand() {
        super("hub",
                "Return to hub",
                "/hub",
                new String[]{"leave", "l", "lobby", "vestibulo", "centro", "exit", "salir"}
        );
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.isPlayer()) return false;
        Player player = (Player) sender;

        String balancedHubService = JBridgeCore.getInstance().getServiceHandler().getBalancedHubServiceId();
        if (balancedHubService.isEmpty()) {
            player.sendMessage(TextFormat.RED + "There are no rotating hub servers!");
            return false;
        }

        JBridgeNukkit.getInstance().transferPlayer(player, balancedHubService);
        return true;
    }
}
