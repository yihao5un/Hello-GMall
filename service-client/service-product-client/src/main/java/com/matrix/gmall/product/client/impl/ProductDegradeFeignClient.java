package com.matrix.gmall.product.client.impl;

import com.alibaba.fastjson.JSONObject;
import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.model.product.BaseCategoryView;
import com.matrix.gmall.model.product.SkuInfo;
import com.matrix.gmall.model.product.SpuSaleAttr;
import com.matrix.gmall.product.client.ProductFeignClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 失败了的话 直接返回空就可以了
 * 注意加上这个@Component注解
 *
 * @Author: yihaosun
 * @Date: 2021/9/20 10:05
 */
@Component
public class ProductDegradeFeignClient implements ProductFeignClient {
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        return null;
    }

    @Override
    public BaseCategoryView getCategoryView(Long category3Id) {
        return null;
    }

    @Override
    public BigDecimal getSkuPrice(Long skuId) {
        return null;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        return null;
    }

    @Override
    public Map<String, Long> getSkuValueIdsMap(Long spuId) {
        return null;
    }

    @Override
    public Result<List<JSONObject>> getBaseCategoryList() {
        return null;
    }
}
