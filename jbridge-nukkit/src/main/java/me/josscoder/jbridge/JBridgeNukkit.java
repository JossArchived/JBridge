package me.josscoder.jbridge;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

public class JBridgeNukkit extends PluginBase {

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
