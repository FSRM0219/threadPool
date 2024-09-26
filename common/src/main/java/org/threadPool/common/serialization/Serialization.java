package org.threadPool.common.serialization;

import lombok.*;

import java.io.Serializable;

/*
 * @Builder注解:类可以使用构建者模式进行实例化,即可以通过链式调用来设置字段,生成Response对象
 * */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Serialization<T> implements Serializable {
    /*
     * RedissonClient将对象作为value存入redis，被存储的对象必须实现序列化接口Serializable
     * */
    private static final long serialVersionUID = -2474596551402989285L;

    private String code;
    private String info;
    private T data;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public enum Code {

        SUCCESS("0000", "success"),
        ERROR("0001", "error"),
        /* 待用 */
        ILLEGAL_PARAMETER("0002", "illegal parameter"),
        ;

        private String code;
        private String info;

    }
}
