package me.josscoder.jbridge.service;

import com.google.gson.Gson;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ServiceInfo {

    private final String id, address, group, region, branch;
    private final int maxPlayers;
    private Gson customData;

    private final Set<String> players = new HashSet<>();

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
        return new ServiceInfo("?", "?", "?", "?", "?", 0);
    }
}
