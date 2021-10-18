package com.matrix.gmall.list.controller;

import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.list.service.SearchService;
import com.matrix.gmall.model.list.Goods;
import com.matrix.gmall.model.list.SearchParam;
import com.matrix.gmall.model.list.SearchResponseVo;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * 创建索引库
 * <p>
 * 上架: MySQL -> ES
 * 下架: 从ES中删除
 *
 * @Author: yihaosun
 * @Date: 2021/10/11 14:12
 */
@RestController
@RequestMapping("api/list")
public class ListApiController {
    /**
     * 引入客户端 底层使用的是 RestHighLevelClient
     */
    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    private SearchService searchService;

    @GetMapping("inner/createIndex")
    public Result<String> createIndex() {
        // 创建索引库和Mapping映射
        restTemplate.createIndex(Goods.class);
        restTemplate.putMapping(Goods.class);
        return Result.ok();
    }

    @GetMapping("inner/upperGoods/{skuId}")
    public Result<String> upperGoods(@PathVariable Long skuId) {
        searchService.upperGoods(skuId);
        return Result.ok();
    }

    @GetMapping("inner/lowerGoods/{skuId}")
    public Result<String> lowerGoods(@PathVariable Long skuId) {
        searchService.lowerGoods(skuId);
        return Result.ok();
    }

    @GetMapping("inner/incrHotScore/{skuId}")
    public Result<String> incrHotScore(@PathVariable("skuId") Long skuId) {
        searchService.incrHotScore(skuId);
        return Result.ok();
    }

    @PostMapping
    public Result<SearchResponseVo> list(@RequestBody SearchParam searchParam) {
        SearchResponseVo searchResponseVo = searchService.search(searchParam);
        return Result.ok(searchResponseVo);
    }
}
