package me.josscoder.jbridge.service;

import lombok.Data;
import me.josscoder.jbridge.JBridgeCore;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Data
public class ServiceInfo {

    private final String id, address, publicAddress, group, region, branch;
    private final int maxPlayers;
    private Set<String> players = new HashSet<>();

    public String getShortId() {
        return id.substring(0, 5);
    }

    public String getGroupAndId() {
        return String.format("%s-%s", group, id);
    }

    public String getRegionGroupAndId() {
        return region + "-" + getGroupAndId();
    }

    public String getGroupAndShortId() {
        return String.format("%s-%s", group, getShortId());
    }

    public String getRegionGroupAndShortId() {
        return region + "-" + getGroupAndShortId();
    }

    public void addPlayer(String player) {
        players.add(player);
    }

    public void removePlayer(String player) {
        players.remove(player);
    }

    public boolean containsPlayer(String player) {
        return players.contains(player);
    }

    public int getPlayersOnline() {
        return players.size();
    }

    public boolean isFull() {
        return getPlayersOnline() >= maxPlayers;
    }

    public static ServiceInfo empty() {
        return new ServiceInfo("?", "?", "?", "?", "?", "?", 0);
    }

    public boolean isEmpty() {
        return equals(ServiceInfo.empty());
    }

    public void pushAdd() {
        CompletableFuture.runAsync(() -> JBridgeCore.getInstance().getServiceHandler().storeService(this));
    }

    public void pushUpdatePlayers() {
        pushUpdateField("players", String.join(",", players));
    }

    public void pushUpdateField(String field, String value) {
        CompletableFuture.runAsync(() -> JBridgeCore.getInstance()
                .getServiceHandler()
                .updateServiceField(getShortId(), field, value)
        );
    }

    public void pushRemove() {
        CompletableFuture.runAsync(() -> JBridgeCore.getInstance().getServiceHandler().removeService(getShortId()));
    }
}
