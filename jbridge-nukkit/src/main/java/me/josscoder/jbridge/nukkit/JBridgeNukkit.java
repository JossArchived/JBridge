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
import me.josscoder.jbridge.nukkit.listener.UpdateDataListener;
import me.josscoder.jbridge.service.ServiceInfo;

import java.util.UUID;

public class JBridgeNukkit extends PluginBase {

    @Getter
    private static JBridgeNukkit instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        setupService();

        registerCommands();
        getServer().getPluginManager().registerEvents(new UpdateDataListener(), this);
    }

    private void setupService() {
        Config config = getConfig();

        JBridgeCore jBridgeCore = new JBridgeCore();

        jBridgeCore.boot(config.getString("redis.hostname"),
                config.getInt("redis.port"),
                config.getString("redis.password"),
                config.getBoolean("debug"),
                new NukkitLogger()
        );

        if (!config.exists("service.id")) {
            config.set("service.id", UUID.randomUUID().toString().substring(0, 8));
            config.save();
        }

        String branch = config.getString("service.branch", "dev");
        String address = branch.startsWith("dev")
                ? "127.0.0.1"
                : config.getString("service.address", getServer().getIp());
        String publicAddress = config.getString("service.publicAddress", address);

        ServiceInfo currentServiceInfo = new ServiceInfo(
                config.getString("service.id"),
                address + ":" + getServer().getPort(),
                publicAddress + ":" + getServer().getPort(),
                config.getString("service.group", "hub"),
                config.getString("service.region", "us"),
                branch,
                getServer().getMaxPlayers()
        );
        currentServiceInfo.pushAdd();

        jBridgeCore.setCurrentServiceInfo(currentServiceInfo);
    }

    private void registerCommands() {
        SimpleCommandMap map = getServer().getCommandMap();
        map.register("transfer", new TransferCommand());
        map.register("whereami", new WhereAmICommand());
    }

    public void networkTransfer(Player player, String serverName) {
        TransferPacket packet = new TransferPacket();
        packet.address = serverName;
        packet.port = 0;

        player.dataPacket(packet);
    }

    @Override
    public void onDisable() {
        JBridgeCore jBridgeCore = JBridgeCore.getInstance();

        try {
            jBridgeCore.getCurrentServiceInfo().pushRemove();
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}

        jBridgeCore.shutdown();
    }
}
