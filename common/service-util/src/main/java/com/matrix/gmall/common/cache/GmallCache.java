package com.matrix.gmall.common.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mqx
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GmallCache {

    //  是否需要定义属性: 锁的前缀！
    String prefix() default "cache";

}
