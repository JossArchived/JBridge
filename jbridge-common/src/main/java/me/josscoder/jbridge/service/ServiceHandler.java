package me.josscoder.jbridge.service;

import lombok.Getter;
import me.josscoder.jbridge.JBridgeCore;

public class ServiceHandler {

    @Getter
    private static ServiceHandler instance;

    public ServiceHandler() {
        instance = this;
    }

    public int getMaxPlayers() {
        return JBridgeCore.getInstance().getServiceInfoMapCache().values()
                .stream()
                .mapToInt(ServiceInfo::getMaxPlayers)
                .sum();
    }
}
