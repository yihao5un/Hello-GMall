package com.matrix.gmall.item.service;

import java.util.Map;

/**
 * @Author: yihaosun
 * @Date: 2021/9/15 21:22
 */
public interface ItemService {

    Map<String, Object> getItemBySkuId(Long skuId);
}
