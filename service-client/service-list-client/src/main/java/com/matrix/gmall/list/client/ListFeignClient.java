package com.matrix.gmall.list.client;

import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.list.client.impl.ListDegradeFeignClient;
import com.matrix.gmall.model.list.SearchParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @Author: yihaosun
 * @Date: 2021/10/12 11:24
 */
@FeignClient(value = "service-list", fallback = ListDegradeFeignClient.class)
public interface ListFeignClient {
    /**
     * 更新商品incrHotScore
     *
     * @param skuId skuId
     * @return Result<String>
     */
    @GetMapping("/api/list/inner/incrHotScore/{skuId}")
    Result<String> incrHotScore(@PathVariable("skuId") Long skuId);

    /**
     * 搜索商品
     *
     * @param listParam listParam
     * @return Result<String>
     */
    @PostMapping("/api/list")
    Result list(@RequestBody SearchParam listParam);

    /**
     * 上架商品
     *
     * @param skuId skuId
     * @return Result<String>
     */
    @GetMapping("/api/list/inner/upperGoods/{skuId}")
    Result<String> upperGoods(@PathVariable("skuId") Long skuId);

    /**
     * 下架商品
     *
     * @param skuId skuId
     * @return Result<String>
     */
    @GetMapping("/api/list/inner/lowerGoods/{skuId}")
    Result<String> lowerGoods(@PathVariable("skuId") Long skuId);
}
