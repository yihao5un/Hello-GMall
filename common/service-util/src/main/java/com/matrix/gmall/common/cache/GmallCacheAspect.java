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
 * @author mqx
 */
@Component
@Aspect
public class GmallCacheAspect {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisTemplate redisTemplate;

    //  切注解：
    @SneakyThrows
    @Around("@annotation(com.matrix.gmall.common.cache.GmallCache)")
    public Object gmallCacheGetData(ProceedingJoinPoint joinPoint){
        Object object = null;
        /*
            1.  获取方法上的注解
            2.  获取到注解的前缀，并组成缓存的key
            3.  根据key 获取缓存中的数据
            4.  判断是否获取到了数据{分布式锁的业务逻辑}
         */
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        GmallCache gmallCache = signature.getMethod().getAnnotation(GmallCache.class);

        //  获取到注解的前缀
        String prefix = gmallCache.prefix();

        //  获取到方法上的参数
        Object[] args = joinPoint.getArgs();
        //  定义缓存的key
        String key = prefix + Arrays.asList(args);

        try {
            //  从缓存获取方法
            object = gitCache(key,signature);
            //  判断
            if (object == null){
                //  分布式锁的业务逻辑上来了！
                //  先加锁
                RLock lock = redissonClient.getLock(key + ":lock");
                //  上锁：
                boolean res = lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);
                //  获取到锁对象
                if (res){
                    try {
                        //  执行被GmallCache 注解表示的方法体中的代码块！
                        object = joinPoint.proceed(joinPoint.getArgs());
                        //  判断 防止缓存穿透
                        if (object==null){
                            Object object1 = new Object();
                            redisTemplate.opsForValue().set(key,JSON.toJSONString(object1),RedisConst.SKUKEY_TEMPORARY_TIMEOUT,TimeUnit.SECONDS);
                            return object1;
                        }
                        //  不为空！
                        //  skuInfo 不为空
                        //  set key value ; 对象，字符串！
                        redisTemplate.opsForValue().set(key, JSON.toJSONString(object),RedisConst.SKUKEY_TIMEOUT,TimeUnit.SECONDS);
                        //  返回数据！
                        return object;
                    }finally {
                        lock.unlock();
                    }
                }else {
                    //  没有获取到锁对象
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return gmallCacheGetData(joinPoint);
                }
            }else {
                //  缓存有了，直接返回！
                return object;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        //  直接返回数据库！
        Object proceed = joinPoint.proceed(joinPoint.getArgs());
        return proceed;
    }

    private Object gitCache(String key,MethodSignature signature) {
        //  返回String
        String sObject = (String) redisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(sObject)){
            //  返回数据！ 获取到返回类型
            //  如果缓存 ： public BigDecimal getSkuPrice(Long skuId) 返回值 BigDecimal
            //  如果缓存：  public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) 返回SpuSaleAttr
            //  如果缓存：  public SkuInfo getSkuInfo(Long skuId) 返回SkuInfo
            Class returnType = signature.getReturnType();

            //  将字符串变为要返回的数据类型！
            return JSON.parseObject(sObject,returnType);
        }
        return null;
    }
}
