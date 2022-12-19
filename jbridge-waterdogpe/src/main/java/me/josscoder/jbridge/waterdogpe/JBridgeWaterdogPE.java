package me.josscoder.jbridge.waterdogpe;

import dev.waterdog.waterdogpe.command.CommandMap;
import dev.waterdog.waterdogpe.event.EventManager;
import dev.waterdog.waterdogpe.event.defaults.PreTransferEvent;
import dev.waterdog.waterdogpe.event.defaults.ProxyPingEvent;
import dev.waterdog.waterdogpe.event.defaults.ProxyQueryEvent;
import dev.waterdog.waterdogpe.logger.Color;
import dev.waterdog.waterdogpe.plugin.Plugin;
import dev.waterdog.waterdogpe.utils.config.Configuration;
import lombok.Getter;
import me.josscoder.jbridge.JBridgeCore;
import me.josscoder.jbridge.service.ServiceInfo;
import me.josscoder.jbridge.waterdogpe.command.ServerListCommand;
import me.josscoder.jbridge.waterdogpe.command.WhereAmICommand;
import me.josscoder.jbridge.waterdogpe.task.ServicePongTask;

import java.util.UUID;

public class JBridgeWaterdogPE extends Plugin {

    @Getter
    private static JBridgeWaterdogPE instance;

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

        ServiceInfo serviceInfo = new ServiceInfo(
                config.getString("service.id", UUID.randomUUID().toString().substring(0, 8)),
                "",
                config.getString("service.group", "proxy"),
                config.getString("service.region", "us"),
                config.getString("service.branch", "dev"),
                -1
        );
        jBridgeCore.setCurrentServiceInfo(serviceInfo);

        handleCommands();
        subscribeEvents();
        getProxy().getScheduler().scheduleRepeating(new ServicePongTask(), 20 * 5, true);
    }

    private void handleCommands() {
        CommandMap map = getProxy().getCommandMap();
        map.unregisterCommand("wdlist");
        map.registerCommand(new WhereAmICommand());
        map.registerCommand(new ServerListCommand());
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
