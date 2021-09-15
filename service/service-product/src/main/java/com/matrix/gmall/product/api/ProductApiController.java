package com.matrix.gmall.product.api;

import com.matrix.gmall.model.product.BaseCategoryView;
import com.matrix.gmall.model.product.SkuInfo;
import com.matrix.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 封装数据提供其他微服务使用
 *
 * 带有inner的URL 表示给内部微服务提供的数据接口
 * 在service-item 用Feign调用接口就能够获取到SkuInfo了
 *
 * @Author: yihaosun
 * @Date: 2021/9/15 21:41
 */
@RestController
@RequestMapping("api/product")
public class ProductApiController {

    @Autowired
    private ManageService manageService;

    @GetMapping("inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId) {
        return manageService.getSkuInfo(skuId);
    }

    @GetMapping("inner/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable Long category3Id) {
        return manageService.getCategoryViewByCategory3Id(category3Id);
    }
}
