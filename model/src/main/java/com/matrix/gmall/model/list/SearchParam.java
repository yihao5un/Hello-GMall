package com.matrix.gmall.model.list;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 封装查询条件
 *
 * @Author yihaosun
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchParam {
    /**
     * ?category3Id=61&trademark=2:华为&props=23:4G:运行内存&order=1:desc
     */
    private Long category1Id;

    private Long category2Id;

    private Long category3Id;

    /**
     * trademark=2:华为
     * 品牌
     */
    private String trademark;

    /**
     * 检索的关键字
     */
    private String keyword;

    /**
     * 排序规则
     * 1:hotScore 2:price
     * 1：综合排序/热点  2：价格
     */
    private String order = "";

    /**
     * props=23:4G:运行内存
     * 平台属性Id 平台属性值名，平台属性名称
     * 页面提交的数组
     */
    private String[] props;

    /**
     * 分页信息
     */
    private Integer pageNo = 1;

    private Integer pageSize = 3;
}
