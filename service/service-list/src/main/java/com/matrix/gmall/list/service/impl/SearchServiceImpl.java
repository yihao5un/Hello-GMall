package com.matrix.gmall.list.service.impl;

import com.matrix.gmall.list.repository.GoodsRepository;
import com.matrix.gmall.list.service.SearchService;
import com.matrix.gmall.model.list.Goods;
import com.matrix.gmall.model.list.SearchAttr;
import com.matrix.gmall.model.product.BaseAttrInfo;
import com.matrix.gmall.model.product.BaseCategoryView;
import com.matrix.gmall.model.product.BaseTrademark;
import com.matrix.gmall.model.product.SkuInfo;
import com.matrix.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
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
}