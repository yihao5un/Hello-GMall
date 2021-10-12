package com.matrix.gmall.list.client;

import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.list.client.impl.ListDegradeFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: yihaosun
 * @Date: 2021/10/12 11:24
 */
@FeignClient(value = "service-list", fallback = ListDegradeFeignClient.class)
public interface ListFeignClient {
    /**
     * 更新商品incrHotScore
     * @param skuId skuId
     * @return Result<String>
     */
    @GetMapping("/api/list/inner/incrHotScore/{skuId}")
    Result<String> incrHotScore(@PathVariable("skuId") Long skuId);
}
