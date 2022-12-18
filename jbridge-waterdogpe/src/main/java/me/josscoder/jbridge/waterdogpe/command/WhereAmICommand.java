package me.josscoder.jbridge.waterdogpe.command;

import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import dev.waterdog.waterdogpe.logger.Color;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.service.ServiceInfo;
import me.josscoder.jbridge.waterdogpe.JBridgeWaterdogPE;

public class WhereAmICommand extends Command {

    public WhereAmICommand() {
        super("whereami", CommandSettings.builder()
                .setDescription("Provide information about the proxy you are on")
                .setAliases(new String[]{"connection"})
                .build()
        );
    }

    @Override
    public boolean onExecute(CommandSender sender, String alias, String[] args) {
        String proxyId = JBridgeWaterdogPE.getInstance().getServiceInfo().getGroupAndId();

        if (!sender.isPlayer()) {
            sender.sendMessage(Color.GOLD + git "You are connected to proxy " + proxyId);
            return true;
        }

        String serviceId = "?";

        ProxiedPlayer player = (ProxiedPlayer) sender;

        ServerInfo serverInfo = player.getServerInfo();
        if (serverInfo != null) serviceId = serverInfo.getServerName();

        ServiceInfo serviceInfo = JBridgeCore.getInstance().getServiceHandler().getService(serviceId);
        if (serverInfo != null) serviceId = serviceInfo.getGroupAndId();

        sender.sendMessage(Color.GOLD + "You are connected to proxy " + proxyId +
                "\n" +
                "You are connected to server " + serviceId
        );
        return true;
    }
}
