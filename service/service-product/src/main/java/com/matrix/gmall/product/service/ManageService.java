package com.matrix.gmall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.matrix.gmall.model.product.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 3.1	先加载所有的一级分类数据！
 * 3.2	通过选择一级分类Id数据加载二级分类数据！
 * 3.3	通过选择二级分类数据加载三级分类数据！
 * 3.4	根据分类Id 加载 平台属性列表！
 *
 * @Author: yihaosun
 * @Date: 2021/8/31 22:16
 */
public interface ManageService {

    /**
     * 获取所有一级分类
     *
     * @return List<BaseCategory1>
     */
    List<BaseCategory1> getBaseCategory1();

    /**
     * 根据以及分类Id获取二级分类数据
     *
     * @param category1Id category1Id
     * @return List<BaseCategory2>
     */
    List<BaseCategory2> getBaseCategory2(Long category1Id);

    /**
     * 根据以及分类Id获取三级分类数据
     *
     * @param category2Id category2Id
     * @return List<BaseCategory3>
     */
    List<BaseCategory3> getBaseCategory3(Long category2Id);

    /**
     * 根据分类Id获取平台属性列表
     *
     * @param category1Id category1Id
     * @param category2Id category2Id
     * @param category3Id category3Id
     * @return List<BaseAttrInfo>
     */
    List<BaseAttrInfo> getBaseAttrInfoList(Long category1Id, Long category2Id, Long category3Id);

    /**
     * 保存平台属性
     *
     * @param baseAttrInfo baseAttrInfo
     * @RequestBody -> 将Json数据转换成Java对象
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 根据平台属性Id 获取平台属性值集合
     *
     * @param attrId attrId
     * @return List<BaseAttrValue>
     */
    List<BaseAttrValue> getAttrValueList(Long attrId);

    /**
     * 提前判断一下属性值是否有属性
     *
     * @param attrId attrId
     * @return BaseAttrInfo
     */
    BaseAttrInfo getBaseAttrInfo(Long attrId);

    /**
     * 根据三级分类Id 获取SpuInfo集合数据
     *
     * @param spuInfo     spuInfo拿到三级分类的Id
     * @param spuInfoPage 分页
     * @return IPage<SpuInfo>
     */
    IPage<SpuInfo> getSpuInfoList(SpuInfo spuInfo, Page<SpuInfo> spuInfoPage);

    /**
     * 获取所有的销售属性列表
     *
     * @return BaseSaleAttr
     */
    List<BaseSaleAttr> getBaseSaleAttrList();

    /**
     * 保存SpuInfo
     *
     * @param spuInfo spuInfo
     */
    void saveSpuInfo(SpuInfo spuInfo);

    /**
     * 回显spuImageList 集合数据
     * 根据spuId查询SpuImageList列表
     *
     * @param spuId spuId
     * @return List<SpuImage>
     */
    List<SpuImage> getSpuImageList(Long spuId);

    /**
     * 回显销售属性和销售属性值(在方法中有
     *     // 销售属性值对象集合
     *     @TableField(exist = false)
     *     List<SpuSaleAttrValue> spuSaleAttrValueList;
     * 获取销售属性值集合的方法)
     *
     * 根据spuId 获取销售属性列表
     * @param spuId spuId
     * @return List<SpuSaleAttr>
     */
    List<SpuSaleAttr> getSpuSaleAttrList(Long spuId);

    /**
     * 保存SkuInfo数据
     * @param skuInfo skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 查询SkuInfo 带分页
     * @param skuInfoPage skuInfoPage
     * @return IPage<SkuInfo>
     */
    IPage<SkuInfo> getSkuInfoList(Page<SkuInfo> skuInfoPage);

    /**
     * 根据skuId上架
     * @param skuId skuId
     */
    void onSale(Long skuId);

    /**
     * 根据skuId下架
     * @param skuId skuId
     */
    void cancelSale(Long skuId);

    /**
     * 根据SkuId 查询 SkuInfo 以及 SkuImageList
     * @param skuId skuId
     * @return SkuInfo
     */
    SkuInfo getSkuInfo(Long skuId);

    /**
     * 根据三级分类Id 查询 分类属性名称
     * @param category3Id 注意这个category3Id 就是上面那个方法的 SkuInfo.id
     * @return BaseCategoryView
     */
    BaseCategoryView getCategoryViewByCategory3Id(Long category3Id);
}
