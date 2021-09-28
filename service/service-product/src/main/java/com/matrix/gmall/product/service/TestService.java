package com.matrix.gmall.product.service;

/**
 * @Author: yihaosun
 * @Date: 2021/9/23 20:46
 */
public interface TestService {
    /**
     * 测试本地锁
     */
    void testLock() throws InterruptedException;

    /**
     * 写写会互斥
     * 读写互斥
     * 写读互斥
     * 读读不互斥
     */

    /**
     *读锁
     * @return String
     */
    String readLock();

    /**
     * 写锁
     * @return String
     */
    String writeLock();
}
