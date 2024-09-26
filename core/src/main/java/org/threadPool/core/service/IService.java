package org.threadPool.core.service;

import org.threadPool.common.entity.ConfigEntity;
import java.util.List;

public interface IService {

    List<ConfigEntity> queryThreadPoolList();

    ConfigEntity queryThreadPoolConfigByName(String threadPoolName);

    void updateThreadPoolConfig(ConfigEntity threadPoolConfigEntity);

}
