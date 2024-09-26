package org.threadPool.core.trigger.listener;

import com.alibaba.fastjson.JSON;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threadPool.core.registry.redis.IRedisRegistry;
import org.threadPool.core.service.IService;
import org.threadPool.common.entity.ConfigEntity;

import java.util.List;

public class ConfigAdjustListener implements MessageListener<ConfigEntity> {

    private final Logger logger = LoggerFactory.getLogger(ConfigAdjustListener.class);

    private final IService Service;

    private final IRedisRegistry redisRegistry;

    public ConfigAdjustListener(IService Service, IRedisRegistry redisRegistry) {
        this.Service = Service;
        this.redisRegistry = redisRegistry;
    }

    @Override
    public void onMessage(CharSequence charSequence, ConfigEntity configEntity) {
        logger.info("动态线程池，调整线程池配置。线程池名称:{} 核心线程数:{} 最大线程数:{}",
                configEntity.getThreadPoolName(),
                configEntity.getPoolSize(),
                configEntity.getMaximumPoolSize());

        Service.updateThreadPoolConfig(configEntity);

        List<ConfigEntity> configEntities = Service.queryThreadPoolList();
        redisRegistry.reportThreadPool(configEntities);

        String threadPoolName = configEntity.getThreadPoolName();
        ConfigEntity configEntity1 = Service.queryThreadPoolConfigByName(threadPoolName);
        /*redisRegistry.reportThreadPoolConfigParameter(configEntity1);*/
        logger.info("动态线程池，上报线程池配置：{}", JSON.toJSONString(configEntity1));
    }
}
