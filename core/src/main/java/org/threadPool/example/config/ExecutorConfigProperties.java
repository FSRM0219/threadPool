package org.threadPool.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "thread.pool.executor.config", ignoreInvalidFields = true)
public class ExecutorConfigProperties {

    private Integer corePoolSize;

    private Integer maxPoolSize;

    private Long keepAliveTime;

    private Integer blockQueueSize;
    /*
     * AbortPolicy：丢弃任务并抛出RejectedExecutionException异常
     * DiscardPolicy：直接丢弃任务，但是不会抛出异常
     * DiscardOldestPolicy：将最早进入队列的任务删除，之后再尝试加入队列的任务被拒绝
     * CallerRunsPolicy：如果任务添加线程池失败，那么主线程自己执行该任务
     * */
    private String policy = "AbortPolicy";
}
