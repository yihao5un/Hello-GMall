package com.matrix.gmall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.model.product.SkuInfo;
import com.matrix.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: yihaosun
 * @Date: 2021/9/12 14:02
 */
@RestController
@RequestMapping("admin/product")
public class SkuManageController {
    @Autowired
    private ManageService manageService;

    @PostMapping("saveSkuInfo")
    public Result<String> saveSkuInfo(@RequestBody SkuInfo skuInfo) {
        manageService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    @GetMapping("list/{page}/{limit}")
    public Result<IPage<SkuInfo>> getSkuInfoList(@PathVariable Long page,
                                 @PathVariable Long limit) {
        Page<SkuInfo> skuInfoPage = new Page<>(page, limit);
        IPage<SkuInfo> iPage = manageService.getSkuInfoList(skuInfoPage);
        return Result.ok(iPage);
    }

    @GetMapping("onSale/{skuId}")
    public Result<String> onSale(@PathVariable Long skuId) {
        manageService.onSale(skuId);
        return Result.ok();
    }

    @GetMapping("cancelSale/{skuId}")
    public Result<String> cancelSale(@PathVariable Long skuId) {
        manageService.cancelSale(skuId);
        return Result.ok();
    }
}
