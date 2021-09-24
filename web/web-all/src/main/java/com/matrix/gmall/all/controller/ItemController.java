package com.matrix.gmall.all.controller;

import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.item.client.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @Author: yihaosun
 * @Date: 2021/9/22 21:51
 */
//@RestController // 不能使用这个否则会直接返回item/index
@Controller // 返回的是视图名称
public class ItemController {
//    @Qualifier("com.matrix.gmall.item.client.ItemFeignClient")
    @Autowired
    private ItemFeignClient itemFeignClient;

    @RequestMapping("{skuId}.html")
    public String skuItem(@PathVariable Long skuId, Model model) {
        Result<Map> result = itemFeignClient.getItemById(skuId);
        // 将商品详情中的所有Map都一次性全部存上
        model.addAllAttributes(result.getData());
        // 返回item目录下的视图名称
        return "item/index";
    }
}
