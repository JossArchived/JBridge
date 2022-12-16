package me.josscoder.jbridge;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import me.josscoder.jbridge.logger.ILogger;
import me.josscoder.jbridge.packet.PacketHandler;
import me.josscoder.jbridge.service.ServiceHandler;
import me.josscoder.jbridge.service.ServiceInfo;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

@Getter
@Setter
public class JBridgeCore {

    @Getter
    private static JBridgeCore instance;

    public static final String SERVICE_CACHE_CHANNEL = "jbridge-service-cache-channel",
            MESSAGE_CHANNEL = "jbridge-message-channel";

    public static final byte[] PACKET_CHANNEL = "jbridge-packet-channel".getBytes(StandardCharsets.UTF_8);

    private boolean debug;
    private ILogger logger;

    private JedisPool jedisPool = null;
    private Gson gson;

    private JedisPubSub serviceCachePubSub = null;
    private BinaryJedisPubSub packetPubSub = null;

    private PacketHandler packetHandler;
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
        this.logger = logger;
        gson = new GsonBuilder().create();

        packetHandler = new PacketHandler();

        ForkJoinPool.commonPool().execute(() -> {
            try {
                try (Jedis jedis = jedisPool.getResource()) {
                    jedis.subscribe(serviceCachePubSub = new JedisPubSub() {
                        @Override
                        public void onMessage(String channel, String message) {
                            ServiceInfo data = gson.fromJson(message, ServiceInfo.class);
                            serviceInfoCache.put(data.getId(), data);
                        }

                        @Override
                        public void onSubscribe(String channel, int subscribedChannels) {
                            if (debug) logger.info("JBridge cache pubSub Started!");
                        }

                        @Override
                        public void onUnsubscribe(String channel, int subscribedChannels) {
                            if (debug) logger.info("JBridge cache pubSub Stopped!");
                        }
                    }, SERVICE_CACHE_CHANNEL);

                    jedis.subscribe(packetPubSub = new BinaryJedisPubSub(){
                        @Override
                        public void onMessage(byte[] channel, byte[] message) {
                            packetHandler.handlePacketDecoding(message);
                        }

                        @Override
                        public void onSubscribe(byte[] channel, int subscribedChannels) {
                            if (debug) logger.info("JBridge packet pubSub Started!");
                        }

                        @Override
                        public void onUnsubscribe(byte[] channel, int subscribedChannels) {
                            if (debug) logger.info("JBridge packet pubSub Stopped!");
                        }
                    }, PACKET_CHANNEL);
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

    public void shutdown() {
        if (serviceCachePubSub != null) serviceCachePubSub.unsubscribe();
        if (packetPubSub != null) packetPubSub.unsubscribe();
        if (jedisPool != null) jedisPool.close();
    }
}
