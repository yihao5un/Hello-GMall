package com.matrix.gmall.common.cache;

import com.alibaba.fastjson.JSON;
import com.matrix.gmall.common.constant.RedisConst;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author yihaosun
 */
@Component
@Aspect
public class GmallCacheAspect {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 切注解
     * <p>
     * AOP 的底层原理是动态代理
     * 切点
     * 切面
     * 通知
     *
     * @Around 能够处理前置通知和后置通知所有的业务逻辑
     * ProceedingJoinPoint 切入点
     */
    @SneakyThrows
    @Around("@annotation(com.matrix.gmall.common.cache.GmallCache)")
    public Object gmallCacheGetData(ProceedingJoinPoint joinPoint) {
        Object object = null;
        /**
         * 1. 获取方法上的注解 -> 看看哪些方法上用到了这个注解
         * 2. 获取到注解的前缀，并组成缓存的key -> 比如: skuPrice:skuId
         * 3. 根据key获取缓存中的数据
         * 4. 判断是否获取到了数据 判断是否需要上锁 -> 分布式锁的业务逻辑
         */

        // 1. 获取方法上的注解 -> 看看哪些方法上用到了这个注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        GmallCache gmallCache = signature.getMethod().getAnnotation(GmallCache.class);

        // 2. 获取到注解的前缀，并组成缓存的key -> 比如: skuPrice:skuId
        String prefix = gmallCache.prefix();

        // 获取方法上的参数
        Object[] args = joinPoint.getArgs();

        // 3. 根据key获取缓存中的数据
        String key = prefix + Arrays.asList(args);

        try {
            //  从缓存获取方法
            object = getCache(key, signature);
            //  判断
            if (object == null) {
                //  分布式锁的业务逻辑上来了！
                //  先加锁
                RLock lock = redissonClient.getLock(key + ":lock");
                //  上锁：
                boolean res = lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);
                //  获取到锁对象
                if (res) {
                    try {
                        // 作为一个公用的部分 执行被GmallCache注解表示的方法体中的代码块！可能是skuInfoDB、skuPrice 或 skuInfo 是不一样的
                        object = joinPoint.proceed(joinPoint.getArgs());
                        // 判断 防止缓存穿透
                        if (object == null) {
                            Object object1 = new Object();
                            redisTemplate.opsForValue().set(key, JSON.toJSONString(object1), RedisConst.SKUKEY_TEMPORARY_TIMEOUT, TimeUnit.SECONDS);
                            return object1;
                        }
                        //  不为空！
                        //  skuInfo 不为空
                        //  set key value ; 对象，字符串！
                        redisTemplate.opsForValue().set(key, JSON.toJSONString(object), RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                        //  返回数据！
                        return object;
                    } finally {
                        lock.unlock();
                    }
                } else {
                    // 没有获取到锁对象
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 自旋
                    return gmallCacheGetData(joinPoint);
                }
            } else {
                //  缓存有了，直接返回！
                return object;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        // 兜底方案 -> 直接返回数据库！
        Object proceed = joinPoint.proceed(joinPoint.getArgs());
        return proceed;
    }

    private Object getCache(String key, MethodSignature signature) {
        // 返回String
        // signature 传入这个参数是为了获取到返回的类型
        String sObject = (String) redisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(sObject)) {
            /**
             * 如果缓存： public BigDecimal getSkuPrice(Long skuId) 返回值 BigDecimal
             * 如果缓存： public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) 返回SpuSaleAttr
             * 如果缓存： public SkuInfo getSkuInfo(Long skuId) 返回SkuInfo
             *
             * 返回数据！ 获取到返回类型
             */
            Class returnType = signature.getReturnType();
            //  将字符串变为要返回的数据类型！
            return JSON.parseObject(sObject, returnType);
        }
        return null;
    }
}
