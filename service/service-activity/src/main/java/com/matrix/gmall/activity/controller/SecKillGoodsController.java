package com.matrix.gmall.activity.controller;

import com.matrix.gmall.activity.service.SecKillGoodsService;
import com.matrix.gmall.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yihaosun
 * @date 2022/2/17 19:47
 */
@RestController
@RequestMapping("/api/activity/seckill")
public class SecKillGoodsController {
    @Autowired
    private SecKillGoodsService secKillGoodsService;

    @GetMapping("findAll")
    public Result<Object> findAll() {
        return Result.ok(secKillGoodsService.findAll());
    }

    @GetMapping("getSeckillGoods/{skuId}")
    public Result getSeckillGoods(@PathVariable Long skuId) {
        return Result.ok(secKillGoodsService.findSeckillGoodsById(skuId));
    }
}
