package me.josscoder.jbridge.waterdogpe.command;

import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import dev.waterdog.waterdogpe.logger.Color;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.service.ServiceHandler;
import me.josscoder.jbridge.service.ServiceInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

public class ServerListCommand extends Command {

    public ServerListCommand() {
        super("wdlist", CommandSettings.builder()
                .setDescription("waterdog.command.list.description")
                .setUsageMessage("waterdog.command.list.usage")
                .setPermission("waterdog.command.list.permission")
                .setAliases(new String[]{"servicelist", "services", "servers", "glist", "rglist"})
                .build()
        );
    }

    @Override
    public boolean onExecute(CommandSender sender, String alias, String[] args) {
        if (args.length >= 1) {
            ServerInfo serverInfo = sender.getProxy().getServerInfo(args[0]);
            sender.sendMessage(serverInfo == null
                    ? Color.RED + "Server not found!"
                    : buildServerList(serverInfo)
            );
            return true;
        }

        List<ServerInfo> servers = new ArrayList<>(sender.getProxy().getServers());
        servers.sort(Comparator.comparing(ServerInfo::getServerName));

        StringBuilder builder = new StringBuilder("§l§9Showing all servers:\n§r");
        for (ServerInfo serverInfo : servers) {
            builder.append(buildServerList(serverInfo)).append("\n").append(Color.RESET);
        }

        builder.append("§7Total online players: ").append(sender.getProxy().getPlayers().size());
        sender.sendMessage(builder.toString());
        return true;
    }

    private String buildServerList(ServerInfo serverInfo) {
        StringJoiner joiner = new StringJoiner(",");
        for (ProxiedPlayer player : serverInfo.getPlayers()) {
            joiner.add(player.getName());
        }

        ServiceHandler serviceHandler = JBridgeCore.getInstance().getServiceHandler();
        ServiceInfo service = serviceHandler.getService(serverInfo.getServerName());

        return String.format("§3[%s] §c%s §a%s-%s §b(%s): §f%s",
                serverInfo.getServerName(),
                service == null ? "?" : service.getGroup().toUpperCase(),
                service == null ? "?" : service.getRegion().toUpperCase(),
                service == null ? "?" : service.getBranch().toUpperCase(),
                serverInfo.getPlayers().size(),
                joiner
        );
    }
}
