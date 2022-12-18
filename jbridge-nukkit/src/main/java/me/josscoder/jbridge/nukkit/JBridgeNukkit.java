package me.josscoder.jbridge.nukkit;

import cn.nukkit.Player;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.network.protocol.TransferPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import lombok.Getter;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.nukkit.command.TransferCommand;
import me.josscoder.jbridge.nukkit.command.WhereAmICommand;
import me.josscoder.jbridge.service.ServiceInfo;
import me.josscoder.jbridge.nukkit.task.ServicePingTask;

import java.util.UUID;

public class JBridgeNukkit extends PluginBase {

    @Getter
    private static JBridgeNukkit instance;

    @Getter
    private ServiceInfo serviceInfo;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Config config = getConfig();

        JBridgeCore jBridgeCore = new JBridgeCore();
        jBridgeCore.boot(config.getString("redis.hostname"),
                config.getInt("redis.port"),
                config.getString("redis.password"),
                config.getBoolean("debug"),
                new NukkitLogger()
        );

        String branch = config.getString("service.branch", "dev");
        String address = config.getString("service.address", getServer().getIp());

        String finalAddress = (branch.startsWith("dev") ? "127.0.0.1" : address);

        serviceInfo = new ServiceInfo(
                config.getString("service.id", UUID.randomUUID().toString().substring(0, 8)),
                finalAddress + ":" + getServer().getPort(),
                config.getString("service.group", "hub"),
                config.getString("service.region", "us"),
                branch,
                getServer().getMaxPlayers()
        );

        registerCommands();
        getServer().getScheduler().scheduleRepeatingTask(new ServicePingTask(), 20, true);
    }

    private void registerCommands() {
        SimpleCommandMap map = getServer().getCommandMap();
        map.register("transfer", new TransferCommand());
        map.register("whereami", new WhereAmICommand());
    }

    public void transferPlayer(Player player, String serverName) {
        TransferPacket packet = new TransferPacket();
        packet.address = serverName;
        packet.port = 0;

        player.dataPacket(packet);
    }

    @Override
    public void onDisable() {
        JBridgeCore.getInstance().shutdown();
    }
}
