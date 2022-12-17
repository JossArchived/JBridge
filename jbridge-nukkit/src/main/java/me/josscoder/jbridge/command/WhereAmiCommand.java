package me.josscoder.jbridge.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import me.josscoder.jbridge.JBridgeNukkit;

public class WhereAmiCommand extends Command {

    public WhereAmiCommand() {
        super("whereami", "Provide information about the server you are on");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        String serverId = JBridgeNukkit.getInstance().getServiceInfo().getGroupAndId();
        sender.sendMessage("You are connected to server " + serverId);
        return true;
    }
}
