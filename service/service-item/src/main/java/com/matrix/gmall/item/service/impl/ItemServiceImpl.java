package com.matrix.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.item.service.ItemService;
import com.matrix.gmall.list.client.ListFeignClient;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 远程调用service-product-client对象 不需要远程去调用数据库
 * 通过这个ProductApiController 返回 封装好的数据
 *
 * @Author: yihaosun
 * @Date: 2021/9/15 21:25
 */
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ProductFeignClient productFeignClient;

    /**
     * ThreadPoolExecutor 会使用到这个配置 ThreadPoolConfig
     */
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private ListFeignClient listFeignClient;

    @Override
    public Map<String, Object> getItemBySkuId(Long skuId) {
        /**
         * 返回Map集合 Thymeleaf渲染 用map存储数据
         * Key: Thymeleaf 获取数据的时候对应的${}
         * Value: 对应的数据
         */
        HashMap<String, Object> result = new HashMap<>();

        /**
         * 改造 -> 使用异步编排 使用多线程
         * 商品详情中的销售属性、销售属性值和图片等信息可以使用多线程的方式去获取
         * 每个线程去完成自己的功能 最终得到的数据一起去汇总给前台页面 提高了网站的性能
         */

        // supplyAsync 有返回值
        // 相当于A B C D 均由 A 产生
        // A 是结果集 -> B C D 并行
        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            // 调用远程服务
            // 1. 获取的数据是SkuInfo + SkuImageList
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            result.put("skuInfo", skuInfo);
            // 返回数据
            return skuInfo;
        }, threadPoolExecutor);

        // 相当于B
        CompletableFuture<Void> categoryViewCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo -> {
            // 2. 获取分类数据
            BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
            result.put("categoryView", categoryView);
        }), threadPoolExecutor);

        // 相当于C
        CompletableFuture<Void> spuSaleAttrListCheckBySkuCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo -> {
            // 3. 获取销售属性和销售属性值
            List<SpuSaleAttr> spuSaleAttrListCheckBySku = productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());
            result.put("spuSaleAttrListCheckBySku", spuSaleAttrListCheckBySku);
        }), threadPoolExecutor);

        // 相当于D
        CompletableFuture<Void> valueJsonCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync((skuInfo -> {
            // 4. 查询销售属性值Id 和 skuId 组合的Map
            Map<String, Long> skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
            // 将Map转换为页面需要的JSON对象
            String valueJson = JSON.toJSONString(skuValueIdsMap);
            result.put("valueJson", valueJson);
        }), threadPoolExecutor);

        CompletableFuture<Void> priceCompletableFuture = CompletableFuture.runAsync(() -> {
            // 5. 获取价格
            BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
            result.put("price", skuPrice);
        }, threadPoolExecutor);

        CompletableFuture<Void> hotScoreCompletableFuture = CompletableFuture.runAsync(() -> {
            // 6. 调用热度排名方法
            listFeignClient.incrHotScore(skuId);
        }, threadPoolExecutor);

        // 使用多任务进行组合
        CompletableFuture.allOf(skuInfoCompletableFuture,
                        categoryViewCompletableFuture,
                        spuSaleAttrListCheckBySkuCompletableFuture,
                        valueJsonCompletableFuture,
                        priceCompletableFuture,
                        hotScoreCompletableFuture)
                .join();

        // 返回map集合 Thymeleaf 渲染: 能用Map存储数据
        return result;
    }
}
