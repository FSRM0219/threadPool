package org.threadPool.core.registry.redis;

import org.threadPool.common.entity.ConfigEntity;
import java.util.List;

public interface IRedisRegistry {

    void reportThreadPool(List<ConfigEntity> threadPoolEntities);

    void reportThreadPoolConfigParameter(ConfigEntity threadPoolConfigEntity);

}
