package com.matrix.gmall.item.service.impl;

import com.matrix.gmall.item.service.ItemService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 远程调用service-product-client对象 不需要远程去调用数据库
 * 通过这个ProductApiController 返回 封装好的数据
 * @Author: yihaosun
 * @Date: 2021/9/15 21:25
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Override
    public Map<String, Object> getItemBySkuId(Long skuId) {
        HashMap<String, Object> result = new HashMap<>();
        return result;
    }
}
