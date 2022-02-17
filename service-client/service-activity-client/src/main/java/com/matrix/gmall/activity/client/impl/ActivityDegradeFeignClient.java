package com.matrix.gmall.activity.client.impl;

import com.matrix.gmall.activity.client.ActivityFeignClient;
import com.matrix.gmall.common.result.Result;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yihaosun
 * @date 2022/2/17 19:54
 */
@Component
public class ActivityDegradeFeignClient implements ActivityFeignClient {
    @Override
    public Result<Object> findAll() {
        return null;
    }

    @Override
    public Result<Object> getSeckillGoods(Long skuId) {
        return null;
    }

    @Override
    public Result<Map<String, Object>> trade() {
        return null;
    }
}
