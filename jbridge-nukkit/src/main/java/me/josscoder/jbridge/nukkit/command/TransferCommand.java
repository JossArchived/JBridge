package me.josscoder.jbridge.nukkit.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.nukkit.JBridgeNukkit;
import me.josscoder.jbridge.service.ServiceHandler;
import me.josscoder.jbridge.service.ServiceInfo;

public class TransferCommand extends Command {

    public TransferCommand() {
        super("transfer",
                "Transfer to a specific server",
                TextFormat.RED + "Usage: /transfer <server>"
        );
        setPermission("jbrdige.command.transfer");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.isPlayer() || !testPermission(sender)) return false;
        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(getUsage());
            return true;
        }

        String serverName = args[0];

        ServiceHandler serviceHandler = JBridgeCore.getInstance().getServiceHandler();
        ServiceInfo service = serviceHandler.getService(serverName);

        if (service == null) {
            player.sendMessage(TextFormat.RED + "That server is currently not in rotation");
            return true;
        }

        JBridgeNukkit.getInstance().transferPlayer(player, service.getShortId());
        return true;
    }
}
