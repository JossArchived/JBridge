package me.josscoder.jbridge;

import dev.waterdog.waterdogpe.command.CommandMap;
import dev.waterdog.waterdogpe.event.EventManager;
import dev.waterdog.waterdogpe.event.defaults.PreTransferEvent;
import dev.waterdog.waterdogpe.event.defaults.ProxyPingEvent;
import dev.waterdog.waterdogpe.event.defaults.ProxyQueryEvent;
import dev.waterdog.waterdogpe.logger.Color;
import dev.waterdog.waterdogpe.plugin.Plugin;
import dev.waterdog.waterdogpe.utils.config.Configuration;
import lombok.Getter;
import me.josscoder.jbridge.command.WhereAmICommand;
import me.josscoder.jbridge.proxyhandler.JoinHandler;
import me.josscoder.jbridge.proxyhandler.ReconnectHandler;
import me.josscoder.jbridge.service.ServiceInfo;
import me.josscoder.jbridge.task.ServicePongTask;

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
                config.getString("service.id", UUID.randomUUID().toString().substring(0, 8)),
                "",
                config.getString("service.group", "proxy"),
                config.getString("service.region", "us"),
                config.getString("service.branch", "dev"),
                -1
        );

        handlerCommands();
        registerHandlers();
        subscribeEvents();
        getProxy().getScheduler().scheduleRepeating(new ServicePongTask(), 20 * 5, true);
    }

    private void handlerCommands() {
        CommandMap map = getProxy().getCommandMap();
        map.unregisterCommand("wdlist");
        map.registerCommand(new WhereAmICommand());
    }

    private void registerHandlers() {
        getProxy().setJoinHandler(new JoinHandler());
        getProxy().setReconnectHandler(new ReconnectHandler());
    }

    private void subscribeEvents() {
        EventManager manager = getProxy().getEventManager();
        manager.subscribe(ProxyPingEvent.class, this::onPing);
        manager.subscribe(ProxyQueryEvent.class, this::onQuery);
        manager.subscribe(PreTransferEvent.class, this::onTransfer);
    }

    private void onPing(ProxyPingEvent event) {
        event.setMaximumPlayerCount(JBridgeCore.getInstance().getServiceHandler().getMaxPlayers());
    }

    private void onQuery(ProxyQueryEvent event) {
        event.setMaximumPlayerCount(JBridgeCore.getInstance().getServiceHandler().getMaxPlayers());
    }

    private void onTransfer(PreTransferEvent event) {
        event.getPlayer().sendMessage(Color.GRAY + "Connecting you to " + event.getTargetServer().getServerName());
    }

    @Override
    public void onDisable() {
        JBridgeCore.getInstance().shutdown();
    }
}
