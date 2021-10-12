package com.matrix.gmall.list.service.impl;

import com.matrix.gmall.list.repository.GoodsRepository;
import com.matrix.gmall.list.service.SearchService;
import com.matrix.gmall.model.list.Goods;
import com.matrix.gmall.model.list.SearchAttr;
import com.matrix.gmall.model.list.SearchParam;
import com.matrix.gmall.model.list.SearchResponseVo;
import com.matrix.gmall.model.product.BaseAttrInfo;
import com.matrix.gmall.model.product.BaseCategoryView;
import com.matrix.gmall.model.product.BaseTrademark;
import com.matrix.gmall.model.product.SkuInfo;
import com.matrix.gmall.product.client.ProductFeignClient;
import lombok.SneakyThrows;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 服务层调用客户端 不调用Mapper 不操作数据库
 * 制作操作ES的接口 GoodsRepository 是自定义数据接口 自己创建的 继承ElasticsearchRepository 就具有了CRUD方法
 *
 * @Author: yihaosun
 * @Date: 2021/10/11 16:23
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public void upperGoods(Long skuId) {
        // TODO 可以使用异步编排远程调用简化代码
        // 先调用远程接口根据skuId得到SkuInfo
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        Goods goods = Goods.builder().build();
        if (Objects.nonNull(skuInfo)) {
            // 先声明一个Goods对象
            goods.setId(skuId);
            goods.setDefaultImg(skuInfo.getSkuDefaultImg());
            goods.setTitle(skuInfo.getSkuName());
            goods.setPrice(skuInfo.getPrice().doubleValue());
            goods.setCreateTime(new Date());

            // 调用远程接口得到品牌数据
            BaseTrademark trademark = productFeignClient.getTrademark(skuInfo.getTmId());
            goods.setTmId(skuInfo.getTmId());
            goods.setTmName(trademark.getTmName());
            goods.setTmLogoUrl(trademark.getLogoUrl());

            // 赋值分类数据
            BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
            goods.setCategory1Id(categoryView.getCategory1Id());
            goods.setCategory2Id(categoryView.getCategory2Id());
            goods.setCategory3Id(categoryView.getCategory3Id());
            goods.setCategory1Name(categoryView.getCategory1Name());
            goods.setCategory2Name(categoryView.getCategory2Name());
            goods.setCategory3Name(categoryView.getCategory3Name());

            // 赋值平台属性集合
            List<BaseAttrInfo> attrList = productFeignClient.getAttrList(skuId);
            List<SearchAttr> searchAttrList = attrList.stream().map(baseAttrInfo -> {
                SearchAttr searchAttr = new SearchAttr();
                searchAttr.setAttrId(baseAttrInfo.getId());
                searchAttr.setAttrName(baseAttrInfo.getAttrName());
                searchAttr.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());
                return searchAttr;
            }).collect(Collectors.toList());
            goods.setAttrs(searchAttrList);
        }
        this.goodsRepository.save(goods);
    }

    @Override
    public void lowerGoods(Long skuId) {
        // 根据 Id 删除
        this.goodsRepository.deleteById(skuId);
    }

    @Override
    public void incrHotScore(Long skuId) {
        /*
         * 借助redis 使用redis 必须考虑
         * 1. 数据类型 zset 自增有序
         * 2. key的名字 set key value
         * 3. 缓存常见的三个问题
         */
        String hotScoreKey = "hotScore";
        Double count = redisTemplate.opsForZSet().incrementScore(hotScoreKey, "skuId:" + skuId, 1);
        if (count % 10 == 0) {
            // 如果是10的倍数 那么我们就更新一次ES
            Optional<Goods> optional = this.goodsRepository.findById(skuId);
            Goods goods = optional.get();
            goods.setHotScore(count.longValue());
            // 保存
            this.goodsRepository.save(goods);
        }
    }

    @SneakyThrows
    @Override
    public SearchResponseVo search(SearchParam searchParam) {
        /*
         1. 根据用户检索的条件产生对应的DSL语句 {方法}
         2. 执行DSL语句并获取到结果集
         3. 将查询到的结果集进行封装到当前的对象 {方法}
        */
        SearchRequest searchRequest = this.buildQueryDsl(searchParam);
        // 调用search方法 查询数据得到结果集
        SearchResponse searchResponse = this.restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 将查询出来的结果集进行封装
        // Total 总记录数在parseSearchResult中去赋值
        SearchResponseVo responseVo = this.parseSearchResult(searchResponse);
        // 默认值是 1
        responseVo.setPageNo(searchParam.getPageNo());
        // 默认值是 3
        responseVo.setPageSize(searchParam.getPageSize());
        // 分页计算方法: 总页数 = 总数 % 每页显示的条数 == 0 ? 总数 / 每页显示的条数 : 总数 / 每页显示的条数 + 1
        // 如下是分页公式
        long totalPage = (responseVo.getTotal() + responseVo.getPageSize() - 1) / responseVo.getPageSize();
        responseVo.setTotalPages(totalPage);
        return responseVo;
    }

    /**
     * 查询数据结果集的转换
     *
     * @param searchResponse searchResponse
     * @return SearchResponseVo
     */
    private SearchResponseVo parseSearchResult(SearchResponse searchResponse) {
        return null;
    }

    /**
     * 生成DSL语句
     *
     * @param searchParam searchParam
     * @return SearchRequest
     */
    private SearchRequest buildQueryDsl(SearchParam searchParam) {
        return null;
    }
}
