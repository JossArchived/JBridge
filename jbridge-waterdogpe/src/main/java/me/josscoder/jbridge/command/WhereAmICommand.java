package me.josscoder.jbridge.command;

import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import dev.waterdog.waterdogpe.logger.Color;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.JBridgeWaterdogPE;
import me.josscoder.jbridge.service.ServiceInfo;

public class WhereAmICommand extends Command {

    public WhereAmICommand() {
        super("whereami", CommandSettings.builder()
                .setPermission("jbrdige.command.whereami")
                .setUsageMessage("/whereami")
                .setDescription("Provide information about the server you are on")
                .build()
        );
    }

    @Override
    public boolean onExecute(CommandSender sender, String alias, String[] args) {
        String proxyId = JBridgeWaterdogPE.getInstance().getServiceInfo().getGroupAndId();

        if (!sender.isPlayer()) {
            sender.sendMessage(Color.BLUE + "You are on proxy " + proxyId);
            return true;
        }

        String serviceId = "?";

        ProxiedPlayer player = (ProxiedPlayer) sender;

        ServerInfo serverInfo = player.getServerInfo();
        if (serverInfo != null) serviceId = serverInfo.getServerName();

        ServiceInfo serviceInfo = JBridgeCore.getInstance().getServiceHandler().getService(serviceId);
        if (serverInfo != null) serviceId = serviceInfo.getGroupAndId();

        sender.sendMessage(Color.BLUE + String.format("You are on server %s on proxy %s", serviceId, proxyId));
        return true;
    }
}
