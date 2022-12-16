package me.josscoder.jbridge;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import me.josscoder.jbridge.logger.ILogger;
import me.josscoder.jbridge.service.ServiceInfo;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class JBridgeCore {

    public static final String SERVICE_CACHE_CHANNEL = "jbridge-service-cache-channel",
            PACKET_CHANNEL = "jbridge-packet-channel";

    @Getter
    @Setter
    private static JedisPool jedisPool;

    @Getter
    @Setter
    private static Gson gson;

    @Getter
    @Setter
    private static boolean debug;

    private static final Cache<String, ServiceInfo> serviceInfoCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();

    public static void boot(String hostname, int port, String password, boolean debug, final ILogger logger) {
        if (getJedisPool() != null) return;

        if (password != null && password.trim().length() > 0) {
            setJedisPool(new JedisPool(new GenericObjectPoolConfig<>(), hostname, port, 5000, password));
        } else {
            setJedisPool(new JedisPool(new GenericObjectPoolConfig<>(), hostname, port, 5000));
        }

        setDebug(debug);

        setGson(new GsonBuilder().create());

        ForkJoinPool.commonPool().execute(() -> {
            try {
                try (Jedis jedis = getJedisPool().getResource()) {
                    jedis.subscribe(new JedisPubSub() {
                        @Override
                        public void onMessage(String channel, String message) {
                            ServiceInfo data = getGson().fromJson(message, ServiceInfo.class);
                            serviceInfoCache.put(data.getId(), data);
                        }

                        @Override
                        public void onSubscribe(String channel, int subscribedChannels) {
                            if (debug) logger.info("JBridge System Started!");
                        }

                        @Override
                        public void onUnsubscribe(String channel, int subscribedChannels) {
                            if (debug) logger.info("JBridge System Stopped!");
                        }
                    }, SERVICE_CACHE_CHANNEL);
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (debug) logger.info("Sleeping for 1 second before reconnecting");
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
        });
    }

    public static Map<String, ServiceInfo> getServiceInfoCache() {
        return serviceInfoCache.asMap();
    }

    public static int getMaxPlayers() {
        return getServiceInfoCache().values()
                .stream()
                .mapToInt(ServiceInfo::getMaxPlayers)
                .sum();
    }
}
