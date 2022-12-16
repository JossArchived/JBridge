package me.josscoder.jbridge.service;

import me.josscoder.jbridge.JBridgeCore;

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
        return getServiceInfoMapCache().get(id);
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

    public List<ServiceInfo> getHubServers() {
        return filterServices(service -> service.getId().startsWith("lobby-") ||
                service.getId().startsWith("hub-")
        );
    }

    public List<ServiceInfo> getSortedServices(List<ServiceInfo> serviceList) {
        return serviceList.stream()
                .sorted(Comparator.comparingInt(ServiceInfo::getPlayersOnline))
                .collect(Collectors.toList());
    }

    public ServiceInfo getBalancedService(List<ServiceInfo> serviceList) {
        return serviceList.size() > 0 ? getSortedServices(serviceList).get(0) : null;
    }

    public String getBalancedServiceId(List<ServiceInfo> serviceList) {
        ServiceInfo serviceInfo = getBalancedService(serviceList);
        return serviceInfo == null ? "" : serviceInfo.getId();
    }

    public ServiceInfo getBalancedHubService() {
        return getBalancedService(getHubServers());
    }

    public String getBalancedHubServiceId() {
        ServiceInfo serviceInfo = getBalancedHubService();
        return serviceInfo == null ? "" : serviceInfo.getId();
    }

    public int getPlayersOnline(String group) {
        return filterServices(service -> service.getGroup().equalsIgnoreCase(group))
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
        return filterServices(service -> service.getGroup().equalsIgnoreCase(group))
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
}
