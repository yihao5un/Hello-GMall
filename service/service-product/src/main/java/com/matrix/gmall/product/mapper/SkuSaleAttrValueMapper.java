package com.matrix.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.matrix.gmall.model.product.SkuSaleAttrValue;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Author: yihaosun
 * @Date: 2021/9/12 22:14
 */
@Mapper
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {
    /**
     * 通过spuId 获取销售属性值对应的skuId 组合
     * @param spuId spuId
     * @return List<Map<String, String>>
     */
    List<Map> selectSaleAttrValuesBySpu(Long spuId);
}
