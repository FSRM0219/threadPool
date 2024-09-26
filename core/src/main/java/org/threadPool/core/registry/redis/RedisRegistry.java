package org.threadPool.core.registry.redis;

import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.threadPool.common.entity.ConfigEntity;
import org.threadPool.common.service.RegistryEnumVO;

import java.time.Duration;
import java.util.List;

public class RedisRegistry implements IRedisRegistry {

    private final RedissonClient redissonClient;

    public RedisRegistry(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void reportThreadPool(List<ConfigEntity> threadPoolEntities) {
        RList<ConfigEntity> list = redissonClient.getList(RegistryEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey());
        list.delete();
        list.addAll(threadPoolEntities);
    }

    @Override
    public void reportThreadPoolConfigParameter(ConfigEntity configEntity) {
        String cacheKey = RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_LIST_KEY.getKey() + "_" + configEntity.getAppName() + "_" + configEntity.getThreadPoolName();
        RBucket<ConfigEntity> bucket = redissonClient.getBucket(cacheKey);
        bucket.set(configEntity, Duration.ofDays(30));
    }
}
