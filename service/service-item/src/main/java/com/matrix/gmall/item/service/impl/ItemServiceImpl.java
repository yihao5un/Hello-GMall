package com.matrix.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.matrix.gmall.item.service.ItemService;
import com.matrix.gmall.model.product.BaseCategoryView;
import com.matrix.gmall.model.product.SkuInfo;
import com.matrix.gmall.model.product.SpuSaleAttr;
import com.matrix.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 远程调用service-product-client对象 不需要远程去调用数据库
 * 通过这个ProductApiController 返回 封装好的数据
 * @Author: yihaosun
 * @Date: 2021/9/15 21:25
 */
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ProductFeignClient productFeignClient;

    @Override
    public Map<String, Object> getItemBySkuId(Long skuId) {
        /**
         * 返回Map集合 Thymeleaf渲染 用map存储数据
         * Key: Thymeleaf 获取数据的时候对应的${}
         * Value: 对应的数据
         */
        HashMap<String, Object> result = new HashMap<>();
        // 调用远程服务
        // 1. 获取的数据是SkuInfo + SkuImageList
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        result.put("skuInfo", skuInfo);
        if (Objects.nonNull(skuInfo)) {
            // 2. 获取分类数据
            BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
            result.put("categoryView", categoryView);
            // 3. 获取销售属性和销售属性值
            List<SpuSaleAttr> spuSaleAttrListCheckBySku = productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());
            result.put("spuSaleAttrListCheckBySku", spuSaleAttrListCheckBySku);
            // 4. 查询销售属性值Id 和 skuId 组合的Map
            Map<String, Long> skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
            // 将Map转换为页面需要的JSON对象
            String valueJson= JSON.toJSONString(skuValueIdsMap);
            result.put("valueJson", valueJson);
        }
        // 5. 获取价格
        BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
        result.put("price", skuPrice);
        return result;
    }
}
