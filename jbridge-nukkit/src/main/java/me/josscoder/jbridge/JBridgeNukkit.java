package me.josscoder.jbridge;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import lombok.Getter;
import me.josscoder.jbridge.service.ServiceInfo;
import redis.clients.jedis.Jedis;

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
        JBridgeCore.boot(config.getString("redis.hostname"),
                config.getInt("redis.port"),
                config.getString("redis.password"),
                config.getBoolean("debug"),
                new NukkitLogger()
        );

        getServer().getScheduler().scheduleRepeatingTask(this, () -> {
            try (Jedis jedis = JBridgeCore.getJedisPool().getResource()) {
                serviceInfo = new ServiceInfo(
                        config.getString("service.id"),
                        config.getString("service.address", getServer().getIp()) + ":" + getServer().getPort(),
                        config.getString("service.group", "hub"),
                        config.getString("service.region", "us"),
                        config.getString("service.branch", "dev"),
                        getServer().getMaxPlayers()
                );
                getServer().getOnlinePlayers().values().forEach(player -> serviceInfo.addPlayer(player.getName()));

                jedis.publish(JBridgeCore.SERVICE_CACHE_CHANNEL, JBridgeCore.getGson().toJson(serviceInfo));
            }
        }, 20, true);
    }
}
