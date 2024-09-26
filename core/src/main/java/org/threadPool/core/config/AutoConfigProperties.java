package org.threadPool.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "redis.config", ignoreInvalidFields = true)
public class AutoConfigProperties {

    private boolean enable;

    private String host;

    private int port;

    private String password;

    // 连接池的大小，默认为64
    private int poolSize = 64;

    // 连接池的最小空闲连接数，默认为24
    private int minIdleSize = 24;

    // 连接的最大空闲时间（单位：毫秒），超过该时间的空闲连接将被关闭，默认为10000
    private int idleTimeout = 10000;

    // 连接超时时间（单位：毫秒），默认为10000
    private int connectTimeout = 10000;

    // 连接重试次数，默认为3
    private int retryAttempts = 3;

    // 连接重试的间隔时间（单位：毫秒），默认为1500
    private int retryInterval = 1500;

    // 定期检查连接是否可用的时间间隔（单位：毫秒），0表示不进行定期检查
    private int pingInterval = 30000;

    // 是否保持长连接，默认为true
    private boolean keepAlive = true;

}
