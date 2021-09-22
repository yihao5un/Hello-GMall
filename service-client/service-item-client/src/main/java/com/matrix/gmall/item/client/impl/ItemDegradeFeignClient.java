package com.matrix.gmall.item.client.impl;

import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.item.client.ItemFeignClient;
import org.springframework.stereotype.Component;

/**
 * @Author: yihaosun
 * @Date: 2021/9/20 12:32
 */
@Component
public class ItemDegradeFeignClient implements ItemFeignClient {
    @Override
    public Result getItemById(Long skuId) {
        return Result.fail();
    }
}
