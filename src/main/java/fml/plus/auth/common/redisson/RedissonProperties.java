package fml.plus.auth.common.redisson;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/* https://github.com/redisson/redisson/wiki/ */

/**
 * Redisson配置属性<br>
 * 使用编码格式: StringCodec  纯字符串编码（无转换)         <br>
 * 其他格式:                                            <br>
 *      - JsonJacksonCodec      Jackson JSON 编码         <br>
 *      - AvroJacksonCodec      Avro 一个二进制的JSON编码   <br>
 *      - SmileJacksonCodec     Smile 另一个二进制的JSON编码 <br>
 *      - CborJacksonCodec      CBOR 又一个二进制的JSON编码  <br>
 *      - MsgPackJacksonCodec   MsgPack 再来一个二进制的JSON编码 <br>
 *      - IonJacksonCodec       Amazon Ion 亚马逊的Ion编码，格式与JSON类似  <br>
 *      - KryoCodec             Kryo 二进制对象序列化编码         <br>
 *      - SerializationCodec    JDK序列化编码                <br>
 *      - FstCodec              FST 10倍于JDK序列化性能而且100%兼容的编码  <br>
 *      - LZ4Codec              LZ4 压缩型序列化对象编码      <br>
 *      - SnappyCodec           Snappy 另一个压缩型序列化对象编码 <br>
 *      - JsonJacksonMapCodec   基于Jackson的映射类使用的编码。可用于避免序列化类的信息，以及用于解决使用byte[]遇到的问题。<br>
 *      - LongCodec             纯整长型数字编码（无转换） <br>
 *      - ByteArrayCodec        字节数组编码  <br>
 *      - CompositeCodec        用来组合多种不同编码在一起<br>
 */
@Data
@ConfigurationProperties(prefix = "spring.redisson")
public class RedissonProperties {

    /**
     * 单节点模式配置
     */
    private SingleClientConfig single;

    /**
     * 线程池数量<br>
     * 这个线程池数量被所有RTopic对象监听器，RRemoteService调用者和RExecutorService任务共同共享。
     */
    private int threads = Runtime.getRuntime().availableProcessors() * 2;
    /**
     * Netty线程池数量<br>
     * 这个线程池数量是在一个Redisson实例内，被其创建的所有分布式数据类型和服务，以及底层客户端所一同共享的线程池里保存的线程数量。
     */
    private int nettyThreads = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * 监控锁的看门狗超时，单位：毫秒<br>
     * 该参数只适用于分布式锁的!!![加锁请求中未明确使用leaseTimeout参数的情况]。
     * 如果该看门口未使用lockWatchdogTimeout去重新调整一个分布式锁的lockWatchdogTimeout超时，那么这个锁将变为失效状态。
     * 这个参数可以用来避免由Redisson客户端节点宕机或其他原因造成死锁的情况。
     */
    private long lockWatchdogTimeout = 30000;

    /**
     * 保持订阅发布顺序<br>
     * 通过该参数来修改是否按订阅发布消息的接收顺序出来消息，如果选否将对消息实行并行处理，该参数只适用于订阅发布消息的情况。
     */
    private boolean keepPubSubOrder = true;

    /**
     * 单节点模式配置
     */
    @Data
    public static class SingleClientConfig {
        /**
         * 节点地址<br>
         * 可以通过host:port的格式来指定节点地址 <br>
         * example: redis://127.0.0.1:6379
         */
        private String address;

        /**
         * 数据库编号
         */
        private int database = 0;

        /**
         * 密码
         */
        private String password = null;

        /**
         * 客户端名称
         */
        private String clientName = "RedissonClient";

        /**
         * 单个连接最大订阅数
         */
        private int subscriptionsPerConnection = 5;

        /**
         * 用于发布和订阅连接的最小保持连接数（长连接）。<br>
         * Redisson内部经常通过发布和订阅来实现许多功能。<br>
         * 长期保持一定数量的发布订阅连接是必须的。
         */
        private int subscriptionConnectionMinimumIdleSize = 1;

        /**
         * 用于发布和订阅连接的连接池最大容量。<br>
         * 连接池的连接数量自动弹性伸缩。
         */
        private int subscriptionConnectionPoolSize = 50;

        /**
         * 最小空闲连接数（长连接）<br>
         * 长期保持一定数量的连接有利于提高瞬时写入反应速度。
         */
        private int connectionMinimumIdleSize = 32;

        /**
         *  连接池最大数量（长连接）<br>
         */
        private int connectionPoolSize = 64;

        /**
         * 连接空闲超时时间，单位：毫秒
         *
         */
        private int idleConnectionTimeout = 10000;

        /**
         * 连接超时时间，单位：毫秒
         */
        private int connectTimeout = 10000;

        /**
         * 命令等待超时时间，单位：毫秒
         */
        private int timeout = 3000;

        /**
         * 命令失败重试次数<br>
         * 如果尝试达到对应次数，仍然不能将命令发送至某个指定的节点时，将抛出错误
         */
        private int retryAttempts = 3;

        /**
         * 命令重试发送时间间隔，单位：毫秒
         */
        private int retryInterval = 1500;

        /**
         * DNS监测时间间隔，单位：毫秒<br>
         * 监测DNS的变化情况的时间间隔。<br>
         * 必须确保JVM DNS缓存的TTL足够低以支持此功能<br>
         * 如果想要禁用, 则设置为-1
         */
        private int dnsMonitoringInterval = 5000;

        /**
         * 启用SSL终端识别
         */
        private boolean sslEnableEndpointIdentification = true;
    }

}
