package com.matrix.gmall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.model.product.SpuInfo;
import com.matrix.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
     * @return
     */
    @GetMapping("{page}/{limit}")
    public Result getSpuInfoList(@PathVariable Long page,
                                 @PathVariable Long limit,
                                 SpuInfo spuInfo
    ) {
        // 分页查询
        Page<SpuInfo> spuInfoPage = new Page<>(page, limit);
        // List<T> getRecords(); 分页对象记录列表
        IPage<SpuInfo> iPage = manageService.getSpuInfoList(spuInfo, spuInfoPage);
        return Result.ok(iPage);
    }

}
