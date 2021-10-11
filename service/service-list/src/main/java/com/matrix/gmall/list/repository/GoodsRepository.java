package com.matrix.gmall.list.repository;

import com.matrix.gmall.model.list.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author: yihaosun
 * @Date: 2021/10/11 16:29
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods, Long> {
}
