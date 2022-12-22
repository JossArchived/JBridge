package me.josscoder.jbridge.service;

import me.josscoder.jbridge.JBridgeCore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ServiceHandler {

    public Map<String, ServiceInfo> getServiceInfoMapCache() {
        return JBridgeCore.getInstance().getServiceInfoCache().asMap();
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

    public List<ServiceInfo> filterServices(Predicate<? super ServiceInfo> predicate) {
        return getServiceInfoMapCache().values()
                .stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public List<ServiceInfo> getGroupServices(String group) {
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
        return getServiceInfoMapCache().values()
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
        return getServiceInfoMapCache().values()
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
        List<ServiceInfo> serviceList = new ArrayList<>();

        groups.forEach(group -> serviceList.addAll(getGroupServices(group)));

        return getSortedServiceFromList(serviceList, sortMode);
    }

    public ServiceInfo getSortedServiceFromGroup(String group, SortMode sortMode) {
        return getSortedServiceFromList(getGroupServices(group), sortMode);
    }

    public ServiceInfo getSortedServiceFromList(List<ServiceInfo> serviceList, SortMode sortMode) {
        return switch (sortMode) {
            case RANDOM -> serviceList.stream()
                    .findAny()
                    .orElse(null);
            case LOWEST -> serviceList.stream()
                    .min(Comparator.comparingInt(ServiceInfo::getPlayersOnline))
                    .orElse(null);
            case FILL -> serviceList.stream()
                    .filter(service -> !service.isFull())
                    .max(Comparator.comparingInt(ServiceInfo::getPlayersOnline))
                    .orElse(null);
        };
    }
}
