package me.josscoder.jbridge.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.JBridgeNukkit;
import me.josscoder.jbridge.service.ServiceHandler;

public class ServerListCommand extends Command {

    public ServerListCommand() {
        super("serverlist",
                "Displays the list of servers",
                "/serverlist",
                new String[]{"servicelist", "services", "servers"}
        );
        setPermission("jbridge.command.serverlist");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        ServiceHandler serviceHandler = JBridgeCore.getInstance().getServiceHandler();

        sender.sendMessage(TextFormat.BLUE + "Server list: ");
        serviceHandler.getServiceInfoMapCache().values().forEach(service -> sender.sendMessage(
                (sender.isPlayer() && service.containsPlayer(sender.getName()) ||
                        service.getId().equalsIgnoreCase(JBridgeNukkit.getInstance().getServiceInfo().getId())
                        ? TextFormat.GOLD
                        : TextFormat.GRAY
                ) + String.format("- %s §c%s §a%s-%s §f(%s/%s)",
                        service.getShortId(),
                        service.getGroup().toUpperCase(),
                        service.getRegion().toUpperCase(),
                        service.getBranch().toUpperCase(),
                        service.getPlayersOnline(),
                        service.getMaxPlayers()
                )
        ));
        sender.sendMessage(TextFormat.GRAY + String.format("There are %s/%s on the network",
                serviceHandler.getPlayersOnline(),
                serviceHandler.getMaxPlayers())
        );
        return true;
    }
}
