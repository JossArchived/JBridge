package me.josscoder.jbridge;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import me.josscoder.jbridge.logger.ILogger;
import me.josscoder.jbridge.packet.PacketPool;
import me.josscoder.jbridge.service.ServiceHandler;
import me.josscoder.jbridge.service.ServiceInfo;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

@Getter
@Setter
public class JBridgeCore {

    @Getter
    private static JBridgeCore instance;

    public static final String SERVICE_CACHE_CHANNEL = "jbridge-service-cache-channel",
            PACKET_CHANNEL = "jbridge-packet-channel";

    private JedisPool jedisPool;
    private Gson gson;
    private boolean debug;
    private PacketPool packetPool;
    private ServiceHandler serviceHandler;

    private final Cache<String, ServiceInfo> serviceInfoCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();

    public JBridgeCore() {
        instance = this;
    }

    public void boot(String hostname, int port, String password, boolean debug, final ILogger logger) {
        if (jedisPool != null) return;

        if (password != null && password.trim().length() > 0) {
            jedisPool = new JedisPool(new GenericObjectPoolConfig<>(), hostname, port, 5000, password);
        } else {
            jedisPool = new JedisPool(new GenericObjectPoolConfig<>(), hostname, port, 5000);
        }

        this.debug = debug;
        gson = new GsonBuilder().create();

        packetPool = new PacketPool();

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
                            if (debug) logger.info("JBridge cache system Started!");
                        }

                        @Override
                        public void onUnsubscribe(String channel, int subscribedChannels) {
                            if (debug) logger.info("JBridge cache system Stopped!");
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

        serviceHandler = new ServiceHandler();
    }
}
