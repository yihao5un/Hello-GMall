package com.matrix.gmall.product.service.impl;

import com.matrix.gmall.product.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 1. 在缓存中存储一个num 初始值为0
 * 2. 利用stringRedisTemplate 获取到的当前num值
 * 3. 如果num不为空则需要对当前的num进行加一操作 之后在写回去
 * 4. 如果num为空的话 直接返回即可
 *
 * 本地锁
 * 测试本地锁；ab -n 5000 -c 100 192.168.200.120:8206/admin/product/test/testLock
 * 属于这个ip下的这个服务端口和地址 在本地加上所之后 上synchronized可以锁住 -> 结果正确
 * 如果运行多个微服务的时候本地锁就出现了局限性
 *
 * 分布式锁
 * 先启动网关 然后访问service-product 让网关去负载均衡 代理 选择实例
 * 经过ab测试后发现 在分布式项目中 本地锁根本就锁不住 会报超时的错误 然后这个num也不对了
 *
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

    /** 本地锁
    @Override
    public synchronized void testLock() {
        // 操作字符串是opsForValue() 获取当前num的值
        String num = stringRedisTemplate.opsForValue().get("num");
        if (StringUtils.isEmpty(num)) {
            return;
        }
        int numValue = Integer.parseInt(num);
        stringRedisTemplate.opsForValue().set("num", String.valueOf(++numValue));
    }
    */

    // 分布式锁 就不需要synchronized了
    @Override
    public void testLock() {
        // 使用setnx命令 判断是否获取到了锁
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent("lock", "ok");
        // 如果获取到了锁
        if (flag) {
            // 执行业务逻辑
            // 操作字符串是opsForValue() 获取当前num的值
            String num = stringRedisTemplate.opsForValue().get("num");
            if (StringUtils.isEmpty(num)) {
                return;
            }
            int numValue = Integer.parseInt(num);
            stringRedisTemplate.opsForValue().set("num", String.valueOf(++numValue));

            // 最后要将这个锁给他释放掉 del key
            stringRedisTemplate.delete("lock");
        }else {
            // 没有获取到锁 等着业务完成
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 等完之后 再看看业务逻辑跑完了没有
            // 自旋
            testLock();
        }
    }
}
