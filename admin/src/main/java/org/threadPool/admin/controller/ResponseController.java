package org.threadPool.admin.controller;

import org.redisson.api.RList;
import org.threadPool.common.entity.ConfigEntity;
import org.threadPool.common.serialization.Serialization;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/dynamic/thread/pool/")
public class ResponseController {

    @Resource
    public RedissonClient redissonClient;

    @RequestMapping(value = "query_thread_pool_list", method = RequestMethod.GET)
    public Serialization<List<ConfigEntity>> queryThreadPoolList() {
        try {
            RList<ConfigEntity> cacheList = redissonClient.getList("THREAD_POOL_CONFIG_LIST_KEY");
            return Serialization.<List<ConfigEntity>>builder()
                    .code(Serialization.Code.SUCCESS.getCode())
                    .info(Serialization.Code.SUCCESS.getInfo())
                    .data(cacheList.readAll())
                    .build();
        } catch (Exception e) {
            log.error("查询线程池数据异常", e);
            return Serialization.<List<ConfigEntity>>builder()
                    .code(Serialization.Code.ERROR.getCode())
                    .info(Serialization.Code.ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "query_thread_pool_config", method = RequestMethod.GET)
    public Serialization<ConfigEntity> queryThreadPoolConfig(@RequestParam String appName, @RequestParam String threadPoolName) {
        try {
            String cacheKey = "THREAD_POOL_CONFIG_PARAMETER_LIST_KEY" + "_" + appName + "_" + threadPoolName;
            ConfigEntity configEntity = redissonClient.<ConfigEntity>getBucket(cacheKey).get();
            return Serialization.<ConfigEntity>builder()
                    .code(Serialization.Code.SUCCESS.getCode())
                    .info(Serialization.Code.SUCCESS.getInfo())
                    .data(configEntity)
                    .build();
        } catch (Exception e) {
            log.error("查询线程池配置异常", e);
            return Serialization.<ConfigEntity>builder()
                    .code(Serialization.Code.ERROR.getCode())
                    .info(Serialization.Code.ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "update_thread_pool_config", method = RequestMethod.POST)
    public Serialization<Boolean> updateThreadPoolConfig(@RequestBody ConfigEntity entity) {
        try {
            log.info("修改线程池配置开始 {} {} {}", entity.getAppName(), entity.getThreadPoolName(), JSON.toJSONString(entity));
            RTopic topic = redissonClient.getTopic("DYNAMIC_THREAD_POOL_REDIS_TOPIC" + "_" + entity.getAppName());
            topic.publish(entity);
            log.info("修改线程池配置完成 {} {}", entity.getAppName(), entity.getThreadPoolName());
            return Serialization.<Boolean>builder()
                    .code(Serialization.Code.SUCCESS.getCode())
                    .info(Serialization.Code.SUCCESS.getInfo())
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("修改线程池配置异常 {}", JSON.toJSONString(entity), e);
            return Serialization.<Boolean>builder()
                    .code(Serialization.Code.ERROR.getCode())
                    .info(Serialization.Code.ERROR.getInfo())
                    .data(false)
                    .build();
        }
    }
}
