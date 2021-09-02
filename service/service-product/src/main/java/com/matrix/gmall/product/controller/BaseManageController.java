package com.matrix.gmall.product.controller;

import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.model.product.BaseAttrInfo;
import com.matrix.gmall.model.product.BaseCategory1;
import com.matrix.gmall.model.product.BaseCategory2;
import com.matrix.gmall.model.product.BaseCategory3;
import com.matrix.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: yihaosun
 * @Date: 2021/9/2 20:36
 */
@RestController
@RequestMapping("admin/product")
public class BaseManageController {
    @Autowired
    private ManageService manageService;

    @GetMapping("/getCategory1")
    public Result<List<BaseCategory1>> getCategory1() {
        return Result.ok(manageService.getBaseCategory1()) ;
    }

    @GetMapping("/getCategory2/{category1Id}")
    public Result<List<BaseCategory2>> getCategory2(@PathVariable Long category1Id) {
        return Result.ok(manageService.getBaseCategory2(category1Id)) ;
    }

    @GetMapping("/getCategory3/{category2Id}")
    public Result<List<BaseCategory3>> getCategory3(@PathVariable Long category2Id) {
        return Result.ok(manageService.getBaseCategory3(category2Id)) ;
    }

    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result<List<BaseAttrInfo>> attrInfoList(@PathVariable Long category1Id,
                                                   @PathVariable Long category2Id,
                                                   @PathVariable Long category3Id
                               ) {
        return Result.ok(manageService.getBaseAttrInfoList(category1Id, category2Id, category3Id));
    }
}
