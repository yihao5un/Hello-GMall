package com.matrix.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.matrix.gmall.model.product.*;
import com.matrix.gmall.product.mapper.*;
import com.matrix.gmall.product.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @Author: yihaosun
 * @Date: 2021/8/31 21:52
 */
@Service
public class ManageServiceImpl implements ManageService {
    /**
     * 服务层调用mapper层
     */
    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;

    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;

    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Override
    public List<BaseCategory1> getBaseCategory1() {
        return baseCategory1Mapper.selectList(null);
    }

    @Override
    public List<BaseCategory2> getBaseCategory2(Long category1Id) {
        return baseCategory2Mapper.selectList(new QueryWrapper<BaseCategory2>().eq("category1_id",category1Id));
    }

    @Override
    public List<BaseCategory3> getBaseCategory3(Long category2Id) {
        return baseCategory3Mapper.selectList(new QueryWrapper<BaseCategory3>().eq("category2_id",category2Id));
    }

    @Override
    public List<BaseAttrInfo> getBaseAttrInfoList(Long category1Id, Long category2Id, Long category3Id) {
        return baseAttrInfoMapper.selectBaseAttrInfoList(category1Id,category2Id,category3Id);
    }

    /**
     * @Transactional
     * 多表操作DMl语句 增加这个注解
     * 1. 如果有异常会回滚！
     * 2. 如果当前代码块中出现了非运行时的异常也发生回滚！
     * 如果有问题的话 debug 的时候从controller开始
     * @param baseAttrInfo baseAttrInfo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        // 判断什么时候修改 什么时候新增 判断baseAttrInfo的Id是否为Null
        if (baseAttrInfo.getId() != null) {
            // 更新
            baseAttrInfoMapper.updateById(baseAttrInfo);
        } else {
            // 新增
            baseAttrInfoMapper.insert(baseAttrInfo);
        }

        // 不知道什么时候新增 什么时候修改？
        // 不知道什么时候 因此要先删除 再新增
        QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<BaseAttrValue>().eq("attr_id", baseAttrInfo.getId());
        baseAttrValueMapper.delete(wrapper);

        // 关联到两张表 base_attr_info base_attr_value 插入操作DML 注意事务！！！
        // 获取到平台属性值的集合
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        // 将对应的Id和值插入到平台属性中去
        if (!CollectionUtils.isEmpty(attrValueList)) {
            attrValueList.forEach(baseAttrValue -> {
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insert(baseAttrValue);
            });
        }
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        return baseAttrValueMapper.selectList(new QueryWrapper<BaseAttrValue>().eq("attr_id", attrId));
    }

    @Override
    public BaseAttrInfo getBaseAttrInfo(Long attrId) {
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectById(attrId);
        if (baseAttrInfo != null) {
            List<BaseAttrValue> attrValueList = getAttrValueList(attrId);
            if (!CollectionUtils.isEmpty(attrValueList)) {
                baseAttrInfo.setAttrValueList(attrValueList);
            }
        }
        return baseAttrInfo;
    }

    @Override
    public IPage<SpuInfo> getSpuInfoList(SpuInfo spuInfo, Page<SpuInfo> spuInfoPage) {
        return spuInfoMapper.selectPage(spuInfoPage, new LambdaQueryWrapper<SpuInfo>()
                .eq(SpuInfo::getCategory3Id, spuInfo.getCategory3Id())
                .orderByDesc(SpuInfo::getId));
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);
    }

    /**
     * 保存的内容
     * 1. spu_info
     * 2. spu_image
     * 3. spu_sale_attr
     * 4. spu_sale_attr_value
     * 多表的时候加上@Transactional()事务
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        spuInfoMapper.insert(spuInfo);
        // 获取图片
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (!CollectionUtils.isEmpty(spuImageList)) {
            spuImageList.forEach(spuImage -> {
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insert(spuImage);
            });
        }
        // 获取当前的销售属性集合
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (!CollectionUtils.isEmpty(spuSaleAttrList)) {
            spuSaleAttrList.forEach(spuSaleAttr -> {
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insert(spuSaleAttr);
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if (!CollectionUtils.isEmpty(spuSaleAttrValueList)) {
                    spuSaleAttrValueList.forEach(spuSaleAttrValue -> {
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                    });
                }
            });
        }
    }
}