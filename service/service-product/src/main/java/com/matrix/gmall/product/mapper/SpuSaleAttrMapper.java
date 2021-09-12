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
}
