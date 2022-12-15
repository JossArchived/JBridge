package me.josscoder.jbridge;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.josscoder.jbridge.logger.ILoggerHandler;
import me.josscoder.jbridge.service.ServiceInfo;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class JBridgeCore {

    public static final String REDIS_CHANNEL = "jbridge";

    @Getter
    private static JedisPool jedisPool;

    @Getter
    private static Gson gson;

    private static final Cache<String, ServiceInfo> serviceInfoCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();

    public static void boot(String hostname, int port, String password, ILoggerHandler logger) {
        if (jedisPool != null) return;

        if (password != null && password.trim().length() > 0) {
            jedisPool = new JedisPool(new GenericObjectPoolConfig<Jedis>(), hostname, port, 5000, password);
        } else {
            jedisPool = new JedisPool(new GenericObjectPoolConfig<Jedis>(), hostname, port, 5000);
        }

        gson = new GsonBuilder().create();

        ForkJoinPool.commonPool().execute(() -> {
            try {
                try (Jedis jedis = jedisPool.getResource()) {
                    jedis.subscribe(new JedisPubSub() {
                        @Override
                        public void onMessage(String channel, String message) {
                            ServiceInfo data = gson.fromJson(message, ServiceInfo.class);
                            serviceInfoCache.put(data.getId(), data);
                        }

                        @Override
                        public void onSubscribe(String channel, int subscribedChannels) {
                            logger.info("JBridge System Started!");
                        }

                        @Override
                        public void onUnsubscribe(String channel, int subscribedChannels) {
                            logger.info("JBridge System Stopped!");
                        }
                    }, REDIS_CHANNEL);
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    logger.info("Sleeping for 1 second before reconnecting");
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
        });
    }

    public static Map<String, ServiceInfo> getServiceInfoCache() {
        return serviceInfoCache.asMap();
    }
}
