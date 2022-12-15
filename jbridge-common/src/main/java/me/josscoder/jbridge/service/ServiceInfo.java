package me.josscoder.jbridge.service;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ServiceInfo {

    private final String id, address, group;
    private final int maxPlayers;

    private final Set<String> players = new HashSet<>();

    public void addPlayer(String player) {
        players.add(player);
    }

    public boolean containsPlayer(String player) {
        return players.contains(player);
    }
}
