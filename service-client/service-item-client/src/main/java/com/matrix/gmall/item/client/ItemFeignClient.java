package com.matrix.gmall.item.client;

import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.item.client.impl.ItemDegradeFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 发送数据接口
 * @Author: yihaosun
 * @Date: 2021/9/20 12:31
 */
@FeignClient(value = "service-item", fallback = ItemDegradeFeignClient.class)
public interface ItemFeignClient {
    /**
     * 发送数据接口
     * @param skuId skuId
     * @return Result
     */
    @GetMapping("api/item/{skuId}")
    Result getItemById(@PathVariable Long skuId);
}
