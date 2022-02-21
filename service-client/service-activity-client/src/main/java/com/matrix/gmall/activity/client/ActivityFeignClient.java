package com.matrix.gmall.activity.client;

import com.matrix.gmall.activity.client.impl.ActivityDegradeFeignClient;
import com.matrix.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @author yihaosun
 * @date 2022/2/17 19:53
 */
@FeignClient(value = "service-activity", fallback = ActivityDegradeFeignClient.class)
public interface ActivityFeignClient {
    /**
     * 返回全部列表
     *
     * @return Result
     */
    @GetMapping("/api/activity/seckill/findAll")
    Result<Object> findAll();

    /**
     * 获取实体
     *
     * @param skuId skuId
     * @return Result
     */
    @GetMapping("/api/activity/seckill/getSeckillGoods/{skuId}")
    Result<Object> getSeckillGoods(@PathVariable("skuId") Long skuId);


    /**
     * 下单
     *
     * @return Result<Map<String, Object>>
     */
    @GetMapping("/api/activity/seckill/auth/trade")
    Result<Map<String, Object>> trade();
}
