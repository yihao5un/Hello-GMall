package com.matrix.gmall.list.client.impl;

import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.list.client.ListFeignClient;
import com.matrix.gmall.model.list.SearchParam;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: yihaosun
 * @Date: 2021/10/12 11:28
 */
@Component
public class ListDegradeFeignClient implements ListFeignClient {
    @Override
    public Result<String> incrHotScore(Long skuId) {
        return null;
    }

    @Override
    public Result list(SearchParam listParam) {
        return null;
    }

    @Override
    public Result<String> upperGoods(Long skuId) {
        return null;
    }

    @Override
    public Result<String> lowerGoods(Long skuId) {
        return null;
    }
}
