package me.josscoder.jbridge;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import lombok.Getter;

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

        Config config = getConfig();
        JBridgeCore.boot(config.getString("redis.hostname"),
                config.getInt("redis.port"),
                config.getString("redis.password"),
                new NukkitLogger()
        );
    }

    @Override
    public void onDisable() {

    }
}
