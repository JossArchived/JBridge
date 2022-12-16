package me.josscoder.jbridge.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.service.ServiceHandler;

public class ServiceListCommand extends Command {

    public ServiceListCommand() {
        super("servicelist",
                "Display the list of active services",
                "/servicelist",
                new String[]{"serverlist"}
        );
        setPermission("jbrdige.command.servicelist");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!sender.isPlayer() || !testPermission(sender)) return false;
        Player player = (Player) sender;

        ServiceHandler serviceHandler = JBridgeCore.getInstance().getServiceHandler();

        player.sendMessage(TextFormat.BLUE + "Service list: ");
        serviceHandler.getServiceInfoMapCache().values().forEach(service -> player.sendMessage(
                (service.containsPlayer(player.getName()) ? TextFormat.GOLD : TextFormat.GRAY)
                + String.format("- %s " + TextFormat.WHITE + "(%s/%s)",
                        service.getRegionGroupAndShortId(),
                        service.getPlayersOnline(),
                        service.getMaxPlayers()
                )
        ));
        return true;
    }
}
