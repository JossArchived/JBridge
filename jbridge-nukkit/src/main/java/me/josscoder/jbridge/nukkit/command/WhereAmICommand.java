package me.josscoder.jbridge.nukkit.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import me.josscoder.jbridge.nukkit.JBridgeNukkit;

public class WhereAmICommand extends Command {

    public WhereAmICommand() {
        super("whereami", "Provide information about the server you are on");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        String serverId = JBridgeNukkit.getInstance().getServiceInfo().getGroupAndId();
        sender.sendMessage("You are connected to server " + serverId);
        return true;
    }
}
