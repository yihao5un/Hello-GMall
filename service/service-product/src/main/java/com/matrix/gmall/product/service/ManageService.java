package com.matrix.gmall.product.service;

import com.matrix.gmall.model.product.*;

import java.util.List;

/**
 *
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
     * @return List<BaseCategory1>
     */
   List<BaseCategory1> getBaseCategory1();

    /**
     * 根据以及分类Id获取二级分类数据
     * @param category1Id category1Id
     * @return List<BaseCategory2>
     */
   List<BaseCategory2> getBaseCategory2(Long category1Id);

    /**
     * 根据以及分类Id获取三级分类数据
     * @param category2Id category2Id
     * @return List<BaseCategory3>
     */
    List<BaseCategory3> getBaseCategory3(Long category2Id);

    /**
     * 根据分类Id获取平台属性列表
     * @param category1Id category1Id
     * @param category2Id category2Id
     * @param category3Id category3Id
     * @return List<BaseAttrInfo>
     */
    List<BaseAttrInfo> getBaseAttrInfoList(Long category1Id, Long category2Id, Long category3Id);
}
