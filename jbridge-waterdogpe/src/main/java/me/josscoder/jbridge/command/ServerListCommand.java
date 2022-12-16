package me.josscoder.jbridge.command;

import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import dev.waterdog.waterdogpe.logger.Color;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.service.ServiceHandler;

public class ServerListCommand extends Command {

    public ServerListCommand() {
        super("wdlist", CommandSettings.builder()
                .setDescription("waterdog.command.list.description")
                .setUsageMessage("waterdog.command.list.usage")
                .setPermission("waterdog.command.list.permission")
                .setAliases(new String[]{"serverlist", "servicelist", "services", "servers"})
                .build()
        );
    }

    @Override
    public boolean onExecute(CommandSender sender, String alias, String[] args) {
        ServiceHandler serviceHandler = JBridgeCore.getInstance().getServiceHandler();

        sender.sendMessage(Color.BLUE + "Server list: ");
        serviceHandler.getServiceInfoMapCache().values().forEach(service -> sender.sendMessage(
                (sender.isPlayer() && service.containsPlayer(sender.getName())
                        ? Color.GOLD
                        : Color.GRAY
                ) + String.format("- %s " + Color.WHITE + "(%s/%s)",
                        service.getGroupAndShortId(),
                        service.getPlayersOnline(),
                        service.getMaxPlayers()
                )
        ));
        return true;
    }
}
