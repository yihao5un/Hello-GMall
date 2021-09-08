package com.matrix.gmall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.matrix.gmall.model.product.BaseTrademark;

/**
 * @Author: yihaosun
 * @Date: 2021/9/8 21:39
 */
public interface BaseTrademarkService extends IService<BaseTrademark> {
    /**
     * 查询所有的品牌数据
     * @param baseTrademarkPage baseTrademarkPage
     * @return IPage
     */
    IPage<BaseTrademark> getBaseTradeMarkList(Page<BaseTrademark> baseTrademarkPage);

    /**
     * 新增品牌信息
     * 注意
     * 继承IService<BaseTrademark> 之后有所有的CRUD方法
     * @param baseTrademark baseTrademark
     */
//    void save(BaseTrademark baseTrademark);
}
