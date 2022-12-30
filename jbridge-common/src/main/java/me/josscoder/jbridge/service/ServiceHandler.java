package me.josscoder.jbridge.service;

import me.josscoder.jbridge.JBridgeCore;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ServiceHandler {

    private static final String HASH_SERVICES = "jbridge-services:%s";

    public Set<ServiceInfo> getServices() {
        return JBridgeCore.getInstance().execute(jedis -> {
            Set<ServiceInfo> data = new HashSet<>();

            Set<String> keys = jedis.keys(String.format(HASH_SERVICES, "*"));
            if (keys.isEmpty()) return new HashSet<>();

            keys.forEach(key -> {
                Map<String, String> map = jedis.hgetAll(key);

                ServiceInfo serviceInfo = new ServiceInfo(map.get("id"),
                        map.get("address"),
                        map.get("publicAddress"),
                        map.get("group"),
                        map.get("region"),
                        map.get("branch"),
                        Integer.parseInt(map.get("maxPlayers"))
                );
                serviceInfo.setPlayers(map.get("players").isEmpty()
                        ? new HashSet<>()
                        : Arrays.stream(map.get("players").split(",")).collect(Collectors.toSet())
                );

                data.add(serviceInfo);
            });

            return data;
        });
    }

    public void updateServiceField(String id, String field, String value) {
        JBridgeCore.getInstance().execute(jedis -> {
            jedis.hset(String.format(HASH_SERVICES, id), field, value);
        });
    }

    public void storeService(ServiceInfo serviceInfo) {
        JBridgeCore.getInstance().execute(jedis -> {
            jedis.hset(String.format(HASH_SERVICES, serviceInfo.getShortId()), new HashMap<String, String>() {{
                put("id", serviceInfo.getId());
                put("address", serviceInfo.getAddress());
                put("publicAddress", serviceInfo.getPublicAddress());
                put("group", serviceInfo.getGroup());
                put("region", serviceInfo.getRegion());
                put("branch", serviceInfo.getBranch());
                put("maxPlayers", String.valueOf(serviceInfo.getMaxPlayers()));
                put("players", "");
            }});
        });
    }

    public void removeService(String id) {
        JBridgeCore.getInstance().execute(jedis -> {
            String hash = String.format(HASH_SERVICES, id);
            if (!jedis.exists(hash)) return;

            String[] map = jedis.hgetAll(hash)
                    .keySet()
                    .toArray(new String[0]);

            jedis.hdel(hash, map);
        });
    }

    public Set<ServiceInfo> filterServices(Predicate<? super ServiceInfo> predicate) {
        return getServices()
                .stream()
                .filter(predicate)
                .collect(Collectors.toSet());
    }

    public ServiceInfo getService(String id) {
        return filterServices(service -> service.getId().startsWith(id))
                .stream()
                .findFirst()
                .orElse(null);
    }

    public boolean containsService(String id) {
        return getService(id) != null;
    }

    public Set<ServiceInfo> getGroupServices(String group) {
        return filterServices(service -> service.getGroup().equalsIgnoreCase(group));
    }

    public boolean groupHasServices(String group) {
        return getGroupServices(group).size() > 0;
    }

    public boolean groupHasService(String group, String id) {
        return getGroupServices(group).stream()
                .anyMatch(service -> service.getId().startsWith(id));
    }

    public boolean groupHasPlayer(String group, String player) {
        return getGroupServices(group).stream()
                .anyMatch(service -> service.containsPlayer(player));
    }

    public int getPlayersOnline(String group) {
        return getGroupServices(group)
                .stream()
                .mapToInt(ServiceInfo::getPlayersOnline)
                .sum();
    }

    public int getPlayersOnline() {
        return getServices()
                .stream()
                .mapToInt(ServiceInfo::getPlayersOnline)
                .sum();
    }

    public int getMaxPlayers(String group) {
        return getGroupServices(group)
                .stream()
                .mapToInt(ServiceInfo::getMaxPlayers)
                .sum();
    }

    public int getMaxPlayers() {
        return getServices()
                .stream()
                .mapToInt(ServiceInfo::getMaxPlayers)
                .sum();
    }

    public enum SortMode {
        RANDOM,
        LOWEST,
        FILL
    }

    public ServiceInfo getSortedServiceFromGroups(List<String> groups, SortMode sortMode) {
        Set<ServiceInfo> serviceList = new HashSet<>();

        groups.forEach(group -> serviceList.addAll(getGroupServices(group)));

        return getSortedServiceFromList(serviceList, sortMode);
    }

    public ServiceInfo getSortedServiceFromGroup(String group, SortMode sortMode) {
        return getSortedServiceFromList(getGroupServices(group), sortMode);
    }

    public ServiceInfo getSortedServiceFromList(Set<ServiceInfo> serviceList, SortMode sortMode) {
        ServiceInfo serviceInfo = null;

        switch (sortMode) {
            case RANDOM:
                serviceInfo = serviceList.stream()
                        .findAny()
                        .orElse(null);
                break;
            case LOWEST:
                serviceInfo = serviceList.stream()
                        .min(Comparator.comparingInt(ServiceInfo::getPlayersOnline))
                        .orElse(null);
                break;
            case FILL:
                serviceInfo = serviceList.stream()
                        .filter(service -> !service.isFull())
                        .max(Comparator.comparingInt(ServiceInfo::getPlayersOnline))
                        .orElse(null);
                break;
        }

        return serviceInfo;
    }
}
