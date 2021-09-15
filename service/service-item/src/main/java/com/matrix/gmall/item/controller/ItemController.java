package com.matrix.gmall.item.controller;

import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: yihaosun
 * @Date: 2021/9/15 21:27
 */
@RestController
@RequestMapping("api/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * 编写一个控制器 提供给web-all使用
     */
    @GetMapping("{skuId}")
    public Result<Map<String, Object>> getItemById(@PathVariable Long skuId) {
        Map<String, Object> map = itemService.getItemBySkuId(skuId);
        return Result.ok(map);
    }
}
