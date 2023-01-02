package fml.plus.auth.common;

import fml.plus.auth.common.redisson.RedissonProperties;
import fml.plus.auth.common.redisson.lock.ILockCurrentUser;
import fml.plus.auth.common.redisson.lock.LockCurrentUserSupport;
import fml.plus.auth.common.redisson.lock.LockProcessor;
import fml.plus.auth.common.redisson.lock.LockService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Slf4j
@Configuration
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonAutoConfiguration {

    @Bean
    public LockService lockService(RedissonClient redissonClient) {
        return new LockService(redissonClient);
    }

    @Bean
    public ILockCurrentUser lockSupport() {
        return new LockCurrentUserSupport();
    }

    @Bean
    public LockProcessor lockProcessor(RedissonClient redisson, ILockCurrentUser lockCurrentUserSupport) {
        return new LockProcessor(lockCurrentUserSupport, redisson);
    }

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson(RedissonProperties properties) {
        Config config = new Config();
        config.setCodec(StringCodec.INSTANCE);
        config.setNettyThreads(properties.getNettyThreads());
        config.setThreads(properties.getThreads());

        RedissonProperties.SingleClientConfig single = properties.getSingle();

        config.useSingleServer()
                .setAddress(single.getAddress())
                .setDatabase(single.getDatabase())
                .setPassword(StringUtils.hasText(single.getPassword()) ? single.getPassword() : null)
                .setClientName(single.getClientName())
                .setSubscriptionsPerConnection(single.getSubscriptionsPerConnection())
                .setSubscriptionConnectionMinimumIdleSize(single.getSubscriptionConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(single.getSubscriptionConnectionPoolSize())
                .setConnectionMinimumIdleSize(single.getConnectionMinimumIdleSize())
                .setConnectionPoolSize(single.getConnectionPoolSize())
                .setIdleConnectionTimeout(single.getIdleConnectionTimeout())
                .setConnectTimeout(single.getConnectTimeout())
                .setTimeout(single.getTimeout())
                .setRetryAttempts(single.getRetryAttempts())
                .setRetryInterval(single.getRetryInterval())
                .setDnsMonitoringInterval(single.getDnsMonitoringInterval())
                .setSslEnableEndpointIdentification(single.isSslEnableEndpointIdentification());

        return Redisson.create(config);
    }
}
