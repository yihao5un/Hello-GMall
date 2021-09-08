package com.matrix.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.matrix.gmall.model.product.BaseTrademark;
import com.matrix.gmall.product.mapper.BaseTradeMarkMapper;
import com.matrix.gmall.product.service.BaseTrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: yihaosun
 * @Date: 2021/9/8 21:42
 */
@Service
public class BaseTrademarkServiceImpl extends ServiceImpl<BaseTradeMarkMapper, BaseTrademark> implements BaseTrademarkService {
    @Autowired
    private BaseTradeMarkMapper baseTradeMarkMapper;

    @Override
    public IPage<BaseTrademark> getBaseTradeMarkList(Page<BaseTrademark> baseTrademarkPage) {
        return baseTradeMarkMapper.selectPage(baseTrademarkPage, new LambdaQueryWrapper<BaseTrademark>().orderByDesc(BaseTrademark::getId));
    }
//    可以继承ServiceImpl<BaseTradeMarkMapper, BaseTrademark> 简化手写的CRUD操作
//    @Override
//    public void save(BaseTrademark baseTrademark) {
//        baseTradeMarkMapper.insert(baseTrademark);
//    }
}
