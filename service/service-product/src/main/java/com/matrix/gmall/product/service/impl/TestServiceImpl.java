package com.matrix.gmall.product.service.impl;

import com.matrix.gmall.product.service.TestService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 1. 在缓存中存储一个num 初始值为0
 * 2. 利用stringRedisTemplate 获取到的当前num值
 * 3. 如果num不为空则需要对当前的num进行加一操作 之后在写回去
 * 4. 如果num为空的话 直接返回即可
 * <p>
 * 本地锁
 * 测试本地锁；ab -n 5000 -c 100 192.168.200.120:8206/admin/product/test/testLock
 * 属于这个ip下的这个服务端口和地址 在本地加上所之后 上synchronized可以锁住 -> 结果正确
 * 如果运行多个微服务的时候本地锁就出现了局限性
 * <p>
 * 分布式锁
 * 先启动网关 然后访问service-product 让网关去负载均衡 代理 选择实例
 * 经过ab测试后发现 在分布式项目中 本地锁根本就锁不住 会报超时的错误 然后这个num也不对了
 *
 * @Author: yihaosun
 * @Date: 2021/9/23 20:46
 */
@Service
public class TestServiceImpl implements TestService {
    /**
     * 这个是操作String的RedisTemplate
     */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 本地锁
     *
     * @Override public synchronized void testLock() {
     * // 操作字符串是opsForValue() 获取当前num的值
     * String num = stringRedisTemplate.opsForValue().get("num");
     * if (StringUtils.isEmpty(num)) {
     * return;
     * }
     * int numValue = Integer.parseInt(num);
     * stringRedisTemplate.opsForValue().set("num", String.valueOf(++numValue));
     * }
     */

    // 分布式锁 就不需要synchronized了
//    @Override
//    public void testLock() {
//        // 1. 使用setnx命令 判断是否获取到了锁
////         Boolean flag = stringRedisTemplate.opsForValue()
////                 .setIfAbsent("lock", "ok");
//
//        // 2. 使用setex命令 可以给锁加一个过期时间
////        Boolean flag = stringRedisTemplate.opsForValue()
////                .setIfAbsent("lock", "ok", 3L, TimeUnit.SECONDS);
//
//        // 3. 使用UUID一个线程会对应一个UUID 防止误删锁 这个UUID在堆中 出现的问题是删除操作缺乏原子性
//        String uuid = UUID.randomUUID().toString();
//        Boolean flag = stringRedisTemplate.opsForValue()
//                .setIfAbsent("lock", uuid, 3L, TimeUnit.SECONDS);
//
//        // 如果获取到了锁
//        if (flag) {
//            // 执行业务逻辑
//            // 操作字符串是opsForValue() 获取当前num的值
//            String num = stringRedisTemplate.opsForValue().get("num");
//            if (StringUtils.isEmpty(num)) {
//                return;
//            }
//            int numValue = Integer.parseInt(num);
//            stringRedisTemplate.opsForValue().set("num", String.valueOf(++numValue));
//
//            // 3. 根据UUID删除锁 如果缓存中的UUID和当前的一致的话 则删除 否则不删除
////            if (stringRedisTemplate.opsForValue().get("lock").equals(uuid)) {
////                // 最后要将这个锁给他释放掉 del key
////                stringRedisTemplate.delete("lock");
////            }
//
//            // 4. 定义一个LUA脚本 去保证删除的原子性 缺点是LUA在集群的条件下就不能保证删除的原子性了
//            String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] " +
//                    "then return redis.call('del', KEYS[1]) " +
//                    "else return 0 end";
//            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
//            // 设置LUA脚本
//            redisScript.setScriptText(luaScript);
//            // 设置返回类型
//            redisScript.setResultType(Long.class);
//            stringRedisTemplate.execute(redisScript, Arrays.asList("lock"), uuid);
//
//        } else {
//            // 没有获取到锁 等着业务完成
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            // 等完之后 再看看业务逻辑跑完了没有
//            // 自旋
//            testLock();
//        }
//    }

    /**
     * 使用步骤
     * 1. 获取RLock对象
     * 2. lock.lock() lock.unlock() 这种是最常见的方式
     */
    @Override
    public void testLock() {
        // 创建锁：
        // String skuId="25";
        // String locKey ="lock:"+skuId;

        // 锁的是每个商品
        RLock lock = redissonClient.getLock("lock");
        // 开始加锁
        // lock.lock();
        // 给锁设置一个过期时间 超过10s之后会自动解锁
        // TODO 这个时间的格式 如果设置为10的话 就会少一个
        lock.lock(10, TimeUnit.SECONDS);
        // 业务逻辑代码
        // 获取数据
        String num = stringRedisTemplate.opsForValue().get("num");
        if (StringUtils.isEmpty(num)) {
            return;
        }
        // 将value 变为int
        int numValue = Integer.parseInt(num);
        // 将num +1 放入缓存
        stringRedisTemplate.opsForValue().set("num", String.valueOf(++numValue));
        // 解锁：
        lock.unlock();
    }
}
