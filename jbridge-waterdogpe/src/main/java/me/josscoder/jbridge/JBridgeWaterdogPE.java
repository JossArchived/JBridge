package me.josscoder.jbridge;

import dev.waterdog.waterdogpe.event.EventManager;
import dev.waterdog.waterdogpe.event.defaults.PreTransferEvent;
import dev.waterdog.waterdogpe.event.defaults.ProxyPingEvent;
import dev.waterdog.waterdogpe.event.defaults.ProxyQueryEvent;
import dev.waterdog.waterdogpe.plugin.Plugin;
import dev.waterdog.waterdogpe.utils.config.Configuration;
import lombok.Getter;
import me.josscoder.jbridge.override.GeneralListener;
import me.josscoder.jbridge.override.handler.JoinHandler;
import me.josscoder.jbridge.override.handler.ReconnectHandler;
import me.josscoder.jbridge.service.ServiceInfo;
import me.josscoder.jbridge.task.PingTask;

import java.util.UUID;

public class JBridgeWaterdogPE extends Plugin {

    @Getter
    private static JBridgeWaterdogPE instance;

    @Getter
    private ServiceInfo serviceInfo;

    @Override
    public void onStartup() {
        instance = this;
    }

    @Override
    public void onEnable() {
        loadConfig();

        Configuration config = getConfig();

        JBridgeCore jBridgeCore = new JBridgeCore();
        jBridgeCore.boot(config.getString("redis.hostname"),
                config.getInt("redis.port"),
                config.getString("redis.password"),
                config.getBoolean("debug"),
                new WaterdogPELogger()
        );

        serviceInfo = new ServiceInfo(
                config.getString("service.id", "proxy-" + UUID.randomUUID().toString().substring(0, 3)),
                "",
                config.getString("service.group", "proxy"),
                config.getString("service.region", "us"),
                config.getString("service.branch", "dev"),
                -1
        );

        registerHandlers();
        subscribeEvents();
        getProxy().getScheduler().scheduleRepeating(new PingTask(), 20 * 5, true);
    }

    private void registerHandlers() {
        getProxy().setJoinHandler(new JoinHandler());
        getProxy().setReconnectHandler(new ReconnectHandler());
    }

    private void subscribeEvents() {
        EventManager manager = getProxy().getEventManager();
        manager.subscribe(ProxyPingEvent.class, GeneralListener::onPing);
        manager.subscribe(ProxyQueryEvent.class, GeneralListener::onQuery);
        manager.subscribe(PreTransferEvent.class, GeneralListener::onTransfer);
    }
}
