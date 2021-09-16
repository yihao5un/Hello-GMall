package com.matrix.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.matrix.gmall.model.product.SpuSaleAttr;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: yihaosun
 * @Date: 2021/9/9 22:07
 */
@Mapper
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {
    /**
     * 根据spuId查询销售属性列表
     * @param spuId spuId
     * @return List<SpuSaleAttr>
     */
    List<SpuSaleAttr> selectSpuSaleAttrList(@Param("spuId") Long spuId);

    /**
     * 根据SkuId 和 SpuId 查询销售属性和销售属性值
     * 注意多个参数 要加上@Param注解
     * @param skuId skuId
     * @param spuId spuId
     * @return List<SpuSaleAttr>
     */
    List<SpuSaleAttr> selectSpuSaleAttListCheckBySku(@Param("skuId") Long skuId, @Param("spuId") Long spuId);
}
