package com.matrix.gmall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.model.product.BaseTrademark;
import com.matrix.gmall.product.service.BaseTrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 品牌管理
 * @Author: yihaosun
 * @Date: 2021/9/8 21:30
 */
@RestController
@RequestMapping("admin/product/baseTrademark")
public class BaseTrademarkController {
    @Autowired
    private BaseTrademarkService baseTrademarkService;

    @GetMapping("{page}/{limit}")
    public Result<IPage<BaseTrademark>> getBaseTradeMarkList(@PathVariable Long page,
                                       @PathVariable Long limit) {
        Page<BaseTrademark> baseTrademarkPage = new Page<>(page, limit);
        // Ipage 下有List<T> getRecords()
        IPage<BaseTrademark> iPage = baseTrademarkService.getBaseTradeMarkList(baseTrademarkPage);
        return Result.ok(iPage);
    }

    @GetMapping("get/{id}")
    public Result<BaseTrademark> getBaseTradeMarkList(@PathVariable Long id) {
        return Result.ok(baseTrademarkService.getById(id));
    }

    @PostMapping("save")
    public Result<Boolean> save(@RequestBody BaseTrademark baseTrademark) {
        // @RequestBody: 将Json转换为POJO
        return Result.ok(baseTrademarkService.save(baseTrademark));
    }

    @PostMapping("update")
    public Result<Boolean> update(@RequestBody BaseTrademark baseTrademark) {
        return Result.ok(baseTrademarkService.updateById(baseTrademark));
    }

    @DeleteMapping("remove/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        return Result.ok(baseTrademarkService.removeById(id));
    }
}
