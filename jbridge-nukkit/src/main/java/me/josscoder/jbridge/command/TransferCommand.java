package me.josscoder.jbridge.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.JBridgeNukkit;

public class TransferCommand extends Command {

    public TransferCommand() {
        super("transfer",
                "Transfer to a specific server",
                "/transfer <serverName>"
        );
        setPermission("jbrdige.command.transfer");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.isPlayer() || !testPermission(sender)) return false;
        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(TextFormat.RED + "Use /serverlist to display the list of servers and their groups");
            return true;
        }

        String serverName = args[0];
        if (!JBridgeCore.getInstance().getServiceHandler().containsService(serverName)) {
            player.sendMessage(TextFormat.RED + "The server is currently not in rotation");
            return true;
        }

        JBridgeNukkit.getInstance().transferPlayer(player, serverName);
        return true;
    }
}
