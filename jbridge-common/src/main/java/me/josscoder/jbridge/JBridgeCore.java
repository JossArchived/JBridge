package me.josscoder.jbridge;

import lombok.Getter;
import lombok.Setter;
import me.josscoder.jbridge.logger.ILogger;
import me.josscoder.jbridge.packet.PacketManager;
import me.josscoder.jbridge.service.ServiceHandler;
import me.josscoder.jbridge.service.ServiceInfo;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
@Setter
public class JBridgeCore {

    @Getter
    private static JBridgeCore instance;

    public static final byte[] PACKET_CHANNEL = "jbridge-packet-channel".getBytes(StandardCharsets.UTF_8);

    private boolean debug;
    private ILogger logger;

    private JedisPool jedisPool = null;

    private BinaryJedisPubSub packetPubSub = null;

    private PacketManager packetManager;
    private ServiceHandler serviceHandler;

    private ServiceInfo currentServiceInfo;
    private String password;

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

        this.password = password;

        this.debug = debug;
        this.logger = logger;

        packetManager = new PacketManager();

        ForkJoinPool.commonPool().execute(() -> {
            try {
                execute(jedis -> {
                    jedis.subscribe(packetPubSub = new BinaryJedisPubSub(){
                        @Override
                        public void onMessage(byte[] channel, byte[] message) {
                            packetManager.handlePacketDecoding(message);
                        }

                        @Override
                        public void onSubscribe(byte[] channel, int subscribedChannels) {
                            logger.info("JBridge packet pubSub Started!");
                        }

                        @Override
                        public void onUnsubscribe(byte[] channel, int subscribedChannels) {
                            logger.info("JBridge packet pubSub Stopped!");
                        }
                    }, PACKET_CHANNEL);
                });
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    logger.info("Sleeping for 1 second before reconnecting");
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
        });

        serviceHandler = new ServiceHandler();
    }

    public ServiceInfo getCurrentServiceInfo() {
        return currentServiceInfo == null ? ServiceInfo.empty() : currentServiceInfo;
    }

    public <T> T execute(Function<Jedis, T> action) {
        if (jedisPool == null) throw new RuntimeException("JedisPool is null");
        try (Jedis jedis = jedisPool.getResource()) {
            if (password != null && !password.isEmpty()) jedis.auth(password);
            return action.apply(jedis);
        }
    }

    public void execute(Consumer<Jedis> action) {
        if (jedisPool == null) throw new RuntimeException("JedisPool is null");

        try (Jedis jedis = jedisPool.getResource()) {
            if (password != null && !password.isEmpty()) jedis.auth(password);
            action.accept(jedis);
        }
    }

    public void shutdown() {
        if (packetPubSub != null) packetPubSub.unsubscribe();
        if (jedisPool != null) jedisPool.close();
    }
}
