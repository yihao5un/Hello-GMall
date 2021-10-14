package com.matrix.gmall.list.service.impl;

import com.alibaba.fastjson.JSON;
import com.matrix.gmall.list.repository.GoodsRepository;
import com.matrix.gmall.list.service.SearchService;
import com.matrix.gmall.model.list.*;
import com.matrix.gmall.model.product.BaseAttrInfo;
import com.matrix.gmall.model.product.BaseCategoryView;
import com.matrix.gmall.model.product.BaseTrademark;
import com.matrix.gmall.model.product.SkuInfo;
import com.matrix.gmall.product.client.ProductFeignClient;
import lombok.SneakyThrows;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
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
            // 如果是10的倍数 那么我们就更  新一次ES
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
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        SearchHits hits = searchResponse.getHits();
        // 获取内层Hits
        SearchHit[] subHits = hits.getHits();
        // 创建一个GoodsList集合
        ArrayList<Goods> goodsList = new ArrayList<>();
        // 赋值goodsList
        if (subHits != null && subHits.length > 0) {
            for (SearchHit subHit : subHits) {
                // sourceAsString 这个是Json数据格式
                String sourceAsString = subHit.getSourceAsString();
                // 转换为Java对象
                Goods goods = JSON.parseObject(sourceAsString, Goods.class);
                // 如果高亮的不为空 那么应该取高亮的数据
                if (subHit.getHighlightFields().get("title") != null) {
                    // 获取到高亮中的数据
                    Text title = subHit.getHighlightFields().get("title").getFragments()[0];
                    goods.setTitle(title.toString());
                }
                goodsList.add(goods);
            }
        }
        // 赋值goodsList集合
        searchResponseVo.setGoodsList(goodsList);
        // 设置品牌数据
        Map<String, Aggregation> aggregationMap = searchResponse.getAggregations().asMap();
        ParsedLongTerms tmIdAgg = (ParsedLongTerms) aggregationMap.get("tmIdAgg");
        List<SearchResponseTmVo> trademarkList = tmIdAgg.getBuckets().stream().map(bucket -> {
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            String keyAsString = ((Terms.Bucket) bucket).getKeyAsString();
            searchResponseTmVo.setTmId(Long.parseLong(keyAsString));
            // 获取到品牌名称
            ParsedStringTerms tmNameAgg = ((Terms.Bucket) bucket).getAggregations().get("tmNameAgg");
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
            searchResponseTmVo.setTmName(tmName);
            // 获取到品牌的URL
            ParsedStringTerms tmLogoUrlAgg = ((Terms.Bucket) bucket).getAggregations().get("tmLogoUrlAgg");
            String tmLogoUrl = tmLogoUrlAgg.getBuckets().get(0).getKeyAsString();
            searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
            // 返回当前对象
            return searchResponseTmVo;
        }).collect(Collectors.toList());

        // 数据类型转换 获取到桶的数据类型集合
        searchResponseVo.setTrademarkList(trademarkList);
        // 获取平台属性集合: 聚合 attrAgg --- nested
        ParsedNested attrAgg = (ParsedNested) aggregationMap.get("attrAgg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
        List<SearchResponseAttrVo> attrsList = attrIdAgg.getBuckets().stream().map(bucket -> {
            SearchResponseAttrVo searchResponseAttrVO = new SearchResponseAttrVo();
            Number keyAsNumber = ((Terms.Bucket) bucket).getKeyAsNumber();
            searchResponseAttrVO.setAttrId(keyAsNumber.longValue());
            ParsedStringTerms attrNameAgg = ((Terms.Bucket) bucket).getAggregations().get("attrNameAgg");
            searchResponseAttrVO.setAttrName(attrNameAgg.getBuckets().get(0).getKeyAsString());
            // 属性值的集合
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attrValueAgg");
            // 获取平台属性值集合
            List<String> attrsValueList = attrValueAgg.getBuckets().stream().map(Terms.Bucket::getKeyAsString).collect(Collectors.toList());
            // 赋值平台属性值
            searchResponseAttrVO.setAttrValueList(attrsValueList);
            // 返回数据
            return searchResponseAttrVO;
        }).collect(Collectors.toList());
        // 赋值平台属性集合
        searchResponseVo.setAttrsList(attrsList);
        // 获取到总条数
        searchResponseVo.setTotal(hits.getTotalHits());
        // 返回数据
        return searchResponseVo;
    }

    /**
     * 生成DSL语句
     *
     * @param searchParam searchParam
     * @return SearchRequest
     */
    private SearchRequest buildQueryDsl(SearchParam searchParam) {
        // 查询器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 1. 判断用户是否根据分类Id进行查询
        if (!StringUtils.isEmpty(searchParam.getCategory1Id())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("category1Id", searchParam.getCategory1Id()));
        }
        if (!StringUtils.isEmpty(searchParam.getCategory2Id())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("category2Id", searchParam.getCategory2Id()));
        }
        if (!StringUtils.isEmpty(searchParam.getCategory3Id())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("category3Id", searchParam.getCategory3Id()));
        }

        // 2. 判断用户是否根据 Keyword 检索 在检索框里面的
        if (!StringUtils.isEmpty(searchParam.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("title", searchParam.getKeyword()).operator(Operator.AND));
        }

        // 3. 有可能会根据品牌Id进行过滤 点击品牌那个图标
        // 格式是: trademark=4:小米
        String trademark = searchParam.getTrademark();
        if (!StringUtils.isEmpty(trademark)) {
            // 进行分割 坑! 不要使用SpringUtils框架的 分割不出来
            String[] split = trademark.split(":");
            if (split != null && split.length == 2) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("tmId", split[0]));
            }
        }

        // 4. 看用户是否通过平台属性值进行过滤
        String[] props = searchParam.getProps();
        if (props != null && props.length > 0) {
            for (String prop : props) {
                // prop 代表的是每个平台属性值过滤的数据
                String[] split = prop.split(":");
                if (split != null && split.length == 3) {
                    // split[0]; 属性Id
                    // split[1]; 属性值名称
                    // 创建两个boolQuery
                    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                    // 嵌套查询子查询
                    BoolQueryBuilder subBoolQuery = QueryBuilders.boolQuery();
                    // 设置平台属性Id
                    subBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", split[0]));
                    // 设置平台属性值名称
                    subBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", split[1]));
                    boolQuery.must(QueryBuilders.nestedQuery("attrs", subBoolQuery, ScoreMode.None));
                    // 将boolQuery放入最外层
                    boolQueryBuilder.filter(boolQuery);
                }
            }
        }

        // 5. {query}
        searchSourceBuilder.query(boolQueryBuilder);

        // 6. 分页
        // 从结果集中第几条数据开始显示
        int from = (searchParam.getPageNo() - 1) * searchParam.getPageSize();
        searchSourceBuilder.from(from);
        // 默认是3 每页显示三条数据
        searchSourceBuilder.size(searchParam.getPageSize());

        // 7. 排序 点的时候排序方式发生变化
        //   1. 表示按照哪个字段进行排序
        //      1. 表示热度排序 hotScore
        //      2. 表示价格排序 price
//        searchSourceBuilder.sort("hotScore", SortOrder.DESC);
        String order = searchParam.getOrder();
        if (!StringUtils.isEmpty(order)) {
            // 声明一个字段
            String field = "";
            String[] split = order.split(":");
            if (split != null && split.length == 2) {
                // 判断按照什么字段进行排序
                switch (split[0]) {
                    case "1":
                        field = "hotScore";
                        break;
                    case "2":
                        field = "price";
                        break;
                    default:
                }
                searchSourceBuilder.sort(field, "asc".equals(split[1]) ? SortOrder.ASC : SortOrder.DESC);
            } else {
                // 不走If 走默认值
                searchSourceBuilder.sort("hotScore", SortOrder.DESC);
            }
        }

        // 8. 编写高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style=color:red>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);

        // 9. 聚合
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders
                .terms("tmIdAgg")
                .field("tmId")
                .subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"))
                .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl"));
        // 品牌聚合
        searchSourceBuilder.aggregation(termsAggregationBuilder);
        // 销售属性聚合
        searchSourceBuilder.aggregation(AggregationBuilders.nested("attrAgg", "attrs")
                .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId"))
                .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
                .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue")));

        // 设置哪些字段显示 哪些字段不显示
        searchSourceBuilder.fetchSource(
                new String[]{"id", "title", "defaultImg", "price"}, null);
        SearchRequest searchRequest = new SearchRequest("goods");
        searchRequest.types("info");
        searchRequest.source(searchSourceBuilder);
        // DSL 语句在这
        String dsl = searchSourceBuilder.toString();
        System.out.println("DSL:\t" + dsl);
        // 返回数据
        return searchRequest;
    }
}
