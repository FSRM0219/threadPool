package org.threadPool.core.trigger.job;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.threadPool.core.registry.redis.IRedisRegistry;
import org.threadPool.core.service.IService;
import org.threadPool.common.entity.ConfigEntity;

import java.util.List;

public class DataReportJob {

    private final Logger logger = LoggerFactory.getLogger(DataReportJob.class);

    private final IService Service;

    private final IRedisRegistry redisRegistry;

    public DataReportJob(IService service, IRedisRegistry redisRegistry) {
        this.Service = service;
        this.redisRegistry = redisRegistry;
    }

    /*
     * 0/20：从0秒开始，每20秒执行一次,
     * * * * * ?：每分钟、每小时、每天、每月、不指定具体的星期
     * */
    @Scheduled(cron = "0/20 * * * * ?")
    public void execReportThreadPoolList() {
        List<ConfigEntity> configEntities = Service.queryThreadPoolList();
        redisRegistry.reportThreadPool(configEntities);

        for (ConfigEntity configEntity : configEntities) {
            redisRegistry.reportThreadPoolConfigParameter(configEntity);
            logger.info("动态线程池，上报线程池配置：{}", JSON.toJSONString(configEntity));
        }

    }
}
