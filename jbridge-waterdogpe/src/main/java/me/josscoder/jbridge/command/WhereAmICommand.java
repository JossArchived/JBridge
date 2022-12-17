package me.josscoder.jbridge.command;

import dev.waterdog.waterdogpe.command.Command;
import dev.waterdog.waterdogpe.command.CommandSender;
import dev.waterdog.waterdogpe.command.CommandSettings;
import me.josscoder.jbridge.JBridgeWaterdogPE;

public class WhereAmICommand extends Command {

    public WhereAmICommand() {
        super("whereami", CommandSettings.builder()
                .setDescription("Provide information about the proxy you are on")
                .build()
        );
    }

    @Override
    public boolean onExecute(CommandSender sender, String alias, String[] args) {
        String proxyId = JBridgeWaterdogPE.getInstance().getServiceInfo().getGroupAndId();
        sender.sendMessage("You are connected to proxy " + proxyId);
        return true;
    }
}
