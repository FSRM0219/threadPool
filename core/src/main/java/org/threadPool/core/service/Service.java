package org.threadPool.core.service;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

import org.threadPool.common.entity.ConfigEntity;

public class Service implements IService {

    private final Logger logger = LoggerFactory.getLogger(Service.class);

    private final String applicationName;

    private final Map<String, ThreadPoolExecutor> threadPoolExecutorMap;

    public Service(String applicationName, Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        this.applicationName = applicationName;
        this.threadPoolExecutorMap = threadPoolExecutorMap;
    }

    @Override
    public List<ConfigEntity> queryThreadPoolList() {

        Set<String> threadPoolNames = threadPoolExecutorMap.keySet();

        List<ConfigEntity> threadPools = new ArrayList<>(threadPoolNames.size());

        for (String threadPoolName : threadPoolNames) {

            ThreadPoolExecutor executor = threadPoolExecutorMap.get(threadPoolName);
            ConfigEntity configEntity = new ConfigEntity(applicationName, threadPoolName);

            configEntity.setCorePoolSize(executor.getCorePoolSize());
            configEntity.setMaximumPoolSize(executor.getMaximumPoolSize());
            configEntity.setActiveCount(executor.getActiveCount());
            configEntity.setPoolSize(executor.getPoolSize());
            configEntity.setQueueType(executor.getQueue().getClass().getSimpleName());
            configEntity.setQueueSize(executor.getQueue().size());
            configEntity.setRemainingCapacity(executor.getQueue().remainingCapacity());

            threadPools.add(configEntity);
        }
        return threadPools;
    }

    @Override
    public ConfigEntity queryThreadPoolConfigByName(String threadPoolName) {
        ThreadPoolExecutor executor = threadPoolExecutorMap.get(threadPoolName);
        if (executor == null) {
            return new ConfigEntity(applicationName, threadPoolName);
        }

        ConfigEntity configEntity = new ConfigEntity(applicationName, threadPoolName);
        configEntity.setCorePoolSize(executor.getCorePoolSize());
        configEntity.setMaximumPoolSize(executor.getMaximumPoolSize());
        configEntity.setActiveCount(executor.getActiveCount());
        configEntity.setPoolSize(executor.getPoolSize());
        configEntity.setQueueType(executor.getQueue().getClass().getSimpleName());
        configEntity.setQueueSize(executor.getQueue().size());
        configEntity.setRemainingCapacity(executor.getQueue().remainingCapacity());

        if (logger.isDebugEnabled()) {
            logger.info("动态线程池，配置查询 应用名:{} 线程名:{} 池化配置:{}", applicationName, threadPoolName, JSON.toJSONString(configEntity));
        }
        return configEntity;
    }

    @Override
    public void updateThreadPoolConfig(ConfigEntity configEntity) {
        if (configEntity == null || !applicationName.equals(configEntity.getAppName())) {
            return;
        }

        String threadPoolName = configEntity.getThreadPoolName();
        ThreadPoolExecutor executor = threadPoolExecutorMap.get(threadPoolName);
        if (executor == null) {
            return;
        }

        executor.setCorePoolSize(configEntity.getCorePoolSize());
        executor.setMaximumPoolSize(configEntity.getMaximumPoolSize());

    }
}
