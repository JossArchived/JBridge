package me.josscoder.jbridge.nukkit.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import me.josscoder.jbridge.nukkit.JBridgeNukkit;

public class WhereAmICommand extends Command {

    public WhereAmICommand() {
        super("whereami",
                "Provide information about the server you are on",
                "/whereami",
                new String[]{"connection"}
        );
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        String serviceId = JBridgeNukkit.getInstance().getServiceInfo().getGroupAndId();
        sender.sendMessage("You are connected to server " + serviceId);
        return true;
    }
}
