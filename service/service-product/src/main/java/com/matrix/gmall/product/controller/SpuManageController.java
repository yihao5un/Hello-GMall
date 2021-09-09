package com.matrix.gmall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.model.product.SpuInfo;
import com.matrix.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @Author: yihaosun
 * @Date: 2021/9/7 21:15
 */
@RestController
@RequestMapping("admin/product")
public class SpuManageController {
    @Autowired
    private ManageService manageService;

    /**
     * SpuInfo
     * @param page 当前页
     * @param limit 当前页显示的条数
     * @param spuInfo 可以传一个POJO
     * @return iPage
     */
    @GetMapping("{page}/{limit}")
    public Result<IPage<SpuInfo>> getSpuInfoList(@PathVariable Long page,
                                 @PathVariable Long limit,
                                 SpuInfo spuInfo
    ) {
        // 分页查询
        Page<SpuInfo> spuInfoPage = new Page<>(page, limit);
        // List<T> getRecords(); 分页对象记录列表
        IPage<SpuInfo> iPage = manageService.getSpuInfoList(spuInfo, spuInfoPage);
        return Result.ok(iPage);
    }

    @GetMapping("baseSaleAttrList")
    public Result baseSaleAttrList() {
        return Result.ok(manageService.getBaseSaleAttrList());
    }

    @PostMapping("saveSpuInfo")
    public void saveSpuInfo(@RequestBody SpuInfo spuInfo) {
        manageService.saveSpuInfo(spuInfo);
    }
}
