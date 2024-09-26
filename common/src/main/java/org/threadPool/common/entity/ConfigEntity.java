package org.threadPool.common.entity;

import lombok.Data;

@Data
public class ConfigEntity {

    private String appName;

    private String threadPoolName;

    private int corePoolSize;

    private int maximumPoolSize;

    private int activeCount;

    private int poolSize;

    private String queueType;

    private int queueSize;

    private int remainingCapacity;

    public ConfigEntity() {
    }

    public ConfigEntity(String appName, String threadPoolName) {
        this.appName = appName;
        this.threadPoolName = threadPoolName;
    }
}
