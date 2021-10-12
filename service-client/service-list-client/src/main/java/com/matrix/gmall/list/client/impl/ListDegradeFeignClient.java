package com.matrix.gmall.list.client.impl;

import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.list.client.ListFeignClient;
import org.springframework.stereotype.Component;

/**
 * @Author: yihaosun
 * @Date: 2021/10/12 11:28
 */
@Component
public class ListDegradeFeignClient implements ListFeignClient {
    @Override
    public Result incrHotScore(Long skuId) {
        return null;
    }
}
