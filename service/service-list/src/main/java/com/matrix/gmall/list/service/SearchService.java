package com.matrix.gmall.list.service;

/**
 * 因为是存放到ES中 所以用不到Mapper
 *
 * @Author: yihaosun
 * @Date: 2021/10/11 16:22
 */
public interface SearchService {
    /**
     * 商品上架
     *
     * @param skuId skuId
     */
    void upperGoods(Long skuId);

    /**
     * 商品下架
     *
     * @param skuId skuId
     */
    void lowerGoods(Long skuId);
}
