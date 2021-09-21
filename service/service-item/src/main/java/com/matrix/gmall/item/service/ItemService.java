package com.matrix.gmall.item.service;

import java.util.Map;

/**
 * @Author: yihaosun
 * @Date: 2021/9/15 21:22
 */
public interface ItemService {

    /**
     * Feign 调用 service-product 数据
     * @param skuId skuId
     * @return Map<String, Object>
     */
    Map<String, Object> getItemBySkuId(Long skuId);
}
