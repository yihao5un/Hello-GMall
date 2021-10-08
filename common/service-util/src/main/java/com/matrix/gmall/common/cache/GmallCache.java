package com.matrix.gmall.common.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yihaosun
 * @Target(ElementType.METHOD) 只在方法上使用
 * @Retention(RetentionPolicy.RUNTIME) 运行时在什么位置生效 RUNTIME 表示在字节码或者JVM中生效
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GmallCache {
    /**
     * 是否需要定义属性: 锁的前缀！区别 比如getSkuInfo getSkuPrice等等...
     * @return String
     */
    String prefix() default "cache";
}
