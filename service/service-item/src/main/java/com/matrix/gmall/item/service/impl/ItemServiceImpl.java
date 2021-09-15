package com.matrix.gmall.item.service.impl;

import com.matrix.gmall.item.service.ItemService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: yihaosun
 * @Date: 2021/9/15 21:25
 */
@Service
public class ItemServiceImpl implements ItemService {

    // 远程调用service-product-client对象

    @Override
    public Map<String, Object> getItemBySkuId(Long skuId) {
        HashMap<String, Object> result = new HashMap<>();

        return result;
    }
}
