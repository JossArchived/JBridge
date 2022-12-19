package me.josscoder.jbridge.nukkit.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.josscoder.jbridge.JBridgeCore;

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
        String serviceId = JBridgeCore.getInstance().getCurrentServiceInfo().getGroupAndId();
        sender.sendMessage(TextFormat.GOLD + "You are connected to server " + serviceId);
        return true;
    }
}
