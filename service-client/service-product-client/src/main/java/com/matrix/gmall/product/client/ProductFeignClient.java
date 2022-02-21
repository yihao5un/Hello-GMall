package com.matrix.gmall.product.client;

import com.alibaba.fastjson.JSONObject;
import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.model.product.*;
import com.matrix.gmall.product.client.impl.ProductDegradeFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 发布到Feign上面
 * 调用 service-product api (注意是控制器中的路径) 中的服务
 * FeignClient 调用的名字value 是service-product 中 application.yml 中的 spring.application.name 中的名字
 * fallback 是指如果失败了的处理
 * 这是一个接口 供service-item 去调用
 * service-product 提供数据 -> service-product-client 提供接口 -> service-product-item 调用接口(需要在pom中引用)
 *
 * @Author: yihaosun
 * @Date: 2021/9/18 11:20
 */
@FeignClient(value = "service-product", fallback = ProductDegradeFeignClient.class)
public interface ProductFeignClient {

    /**
     * 根据skuId获取sku信息
     * <p>
     * 注意 要将路径补齐 api/product
     *
     * @param skuId skuId
     * @return SkuInfo
     */
    @GetMapping("api/product/inner/getSkuInfo/{skuId}")
    SkuInfo getSkuInfo(@PathVariable Long skuId);

    /**
     * 通过三级分类id查询分类信息
     *
     * @PathVariable("category3Id") 这个括号里面的是默认值
     * @param category3Id category3Id
     * @return BaseCategoryView
     */
    @GetMapping("/api/product/inner/getCategoryView/{category3Id}")
    BaseCategoryView getCategoryView(@PathVariable Long category3Id);

    /**
     * 获取sku最新价格
     *
     * @param skuId skuId
     * @return BigDecimal
     */
    @GetMapping("/api/product/inner/getSkuPrice/{skuId}")
    BigDecimal getSkuPrice(@PathVariable Long skuId);

    /**
     * 根据spuId，skuId 查询销售属性集合
     *
     * @param skuId skuId
     * @param spuId spuId
     * @return List<SpuSaleAttr>
     */
    @GetMapping("/api/product/inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable Long skuId, @PathVariable Long spuId);

    /**
     * 根据spuId 查询map 集合属性
     *
     * @param spuId spuId
     * @return Map
     */
    @GetMapping("/api/product/inner/getSkuValueIdsMap/{spuId}")
    Map<String, Long> getSkuValueIdsMap(@PathVariable Long spuId);

    /**
     * 获取全部分类信息
     * @return Result<List<JSONObject>>
     */
    @GetMapping("/api/product/getBaseCategoryList")
    Result<List<JSONObject>> getBaseCategoryList();

    /**
     * 通过品牌Id 集合来查询数据
     * @param tmId tmId
     * @return BaseTrademark
     */
    @GetMapping("/api/product/inner/getTrademark/{tmId}")
    BaseTrademark getTrademark(@PathVariable("tmId")Long tmId);
    /**
     * 通过skuId 集合来查询数据
     * @param skuId skuId
     * @return List<BaseAttrInfo>
     */
    @GetMapping("/api/product/inner/getAttrList/{skuId}")
    List<BaseAttrInfo> getAttrList(@PathVariable("skuId") Long skuId);
}
