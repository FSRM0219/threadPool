package org.threadPool.core.config;

import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.threadPool.common.service.RegistryEnumVO;
import org.threadPool.core.service.IService;
import org.threadPool.core.service.Service;
import org.threadPool.common.entity.ConfigEntity;
import org.threadPool.core.registry.redis.IRedisRegistry;
import org.threadPool.core.registry.redis.RedisRegistry;
import org.threadPool.core.trigger.job.DataReportJob;
import org.threadPool.core.trigger.listener.ConfigAdjustListener;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/*
 * @EnableScheduling：允许在Spring应用上下文中自动启用定时任务的支持，
 * 与@Scheduled 结合使用，定义带有@Scheduled注解的方法来实现定时任务
 * */
@Configuration
@EnableConfigurationProperties(AutoConfigProperties.class)
@EnableScheduling
public class AutoConfig {

    private final Logger logger = LoggerFactory.getLogger(AutoConfig.class);

    private String applicationName;

    @Bean("redissonClient")
    public RedissonClient redissonClient(AutoConfigProperties properties) {
        Config config = new Config();

        config.setCodec(JsonJacksonCodec.INSTANCE);

        config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                .setConnectionPoolSize(properties.getPoolSize())
                .setConnectionMinimumIdleSize(properties.getMinIdleSize())
                .setIdleConnectionTimeout(properties.getIdleTimeout())
                .setConnectTimeout(properties.getConnectTimeout())
                .setRetryAttempts(properties.getRetryAttempts())
                .setRetryInterval(properties.getRetryInterval())
                .setPingConnectionInterval(properties.getPingInterval())
                .setKeepAlive(properties.isKeepAlive());

        RedissonClient redissonClient = Redisson.create(config);

        logger.info("动态线程池，注册器（redis）链接初始化完成。{} {} {}", properties.getHost(), properties.getPoolSize(), !redissonClient.isShutdown());

        return redissonClient;
    }

    @Bean
    public IRedisRegistry redisRegistry(RedissonClient redissonClient) {
        return new RedisRegistry(redissonClient);
    }

    @Bean
    public IService service(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutorMap, RedissonClient redissonClient) {
        applicationName = applicationContext.getEnvironment().getProperty("application.name");

        if (StringUtils.isBlank(applicationName)) {
            applicationName = "缺省的";
            logger.warn("动态线程池，启动提示。SpringBoot 应用未配置 spring.application.name 无法获取到应用名称！");
        }

        // 获取缓存数据，更新本地线程池配置
        Set<String> threadPoolKeys = threadPoolExecutorMap.keySet();
        for (String threadPoolKey : threadPoolKeys) {
            // 获取缓存数据
            ConfigEntity configEntity = redissonClient.<ConfigEntity>getBucket(RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + applicationName + "_" + threadPoolKey).get();
            if (null == configEntity) {
                continue;
            }
            ThreadPoolExecutor executor = threadPoolExecutorMap.get(threadPoolKey);
            executor.setCorePoolSize(configEntity.getCorePoolSize());
            executor.setMaximumPoolSize(configEntity.getMaximumPoolSize());
        }

        return new Service(applicationName, threadPoolExecutorMap);
    }

    @Bean
    public DataReportJob dataReportJob(IService service, IRedisRegistry redisRegistry) {
        return new DataReportJob(service, redisRegistry);
    }

    @Bean
    public ConfigAdjustListener configAdjustListener(IService service, IRedisRegistry redisRegistry) {
        return new ConfigAdjustListener(service, redisRegistry);
    }

    @Bean
    public RTopic threadPoolConfigAdjustListener(RedissonClient redissonClient, ConfigAdjustListener configAdjustListener) {
        RTopic topic = redissonClient.getTopic(RegistryEnumVO.DYNAMIC_THREAD_POOL_REDIS_TOPIC.getKey() + "_" + applicationName);
        topic.addListener(ConfigEntity.class, configAdjustListener);
        return topic;
    }
}
