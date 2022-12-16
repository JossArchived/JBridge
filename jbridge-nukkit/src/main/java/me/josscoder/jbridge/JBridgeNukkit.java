package me.josscoder.jbridge;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import lombok.Getter;
import me.josscoder.jbridge.packet.base.ServiceDataUpdatePacket;
import me.josscoder.jbridge.service.ServiceInfo;

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

        String group = config.getString("service.group", "hub");
        String branch = config.getString("service.branch", "dev");

        serviceInfo = new ServiceInfo(
                config.getString("service.id", group + "-" + UUID.randomUUID().toString().substring(0, 3)),
                (branch.startsWith("dev")
                        ? "127.0.0.1"
                        : config.getString("service.address", getServer().getIp())
                ) + ":" + getServer().getPort(),
                group,
                config.getString("service.region", "us"),
                branch,
                getServer().getMaxPlayers()
        );

        getServer().getScheduler().scheduleRepeatingTask(this, () -> {
            serviceInfo.getPlayers().clear();
            getServer().getOnlinePlayers().values().forEach(player -> serviceInfo.addPlayer(player.getName()));

            jBridgeCore.getPacketManager().publishPacket(new ServiceDataUpdatePacket(){{
                    data = jBridgeCore.getGson().toJson(serviceInfo);
            }});
        }, 20, true);
    }

    @Override
    public void onDisable() {
        JBridgeCore.getInstance().shutdown();
    }
}
