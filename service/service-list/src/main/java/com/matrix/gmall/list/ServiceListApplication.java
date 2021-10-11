package com.matrix.gmall.list;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * Elasticsearch DSL 基本操作:
 *
 * GET _search
 * {
 *   "query": {
 *     "match_all": {}
 *   }
 * }
 *
 * # 查询
 * GET /goods/info/_search
 *
 * GET /goods/info/_search
 * {
 *   "query": {
 *     "match": {
 *       "title": "荣耀"
 *     }
 *   }
 * }
 *
 * # 过滤
 * GET /goods/info/_search
 * {
 *   "query": {
 *     "bool": {
 *       "filter": {
 *         "term": {
 *           "tmId": "2"
 *         }
 *       }
 *     }
 *   }
 * }
 *
 * # 分页 from size 和 limit是一样的
 * GET /goods/info/_search
 * {
 *   "query": {
 *     "bool": {
 *       "filter": {
 *         "term": {
 *           "tmId": "2"
 *         }
 *       }
 *     }
 *   },
 *   "from": 0
 *   , "size": 1
 * }
 *
 * # 排序
 * GET /goods/info/_search
 * {
 *   "query": {
 *     "bool": {
 *       "filter": {
 *         "term": {
 *           "tmId": "2"
 *         }
 *       }
 *     }
 *   },
 *   "from": 0
 *   , "size": 20
 *   , "sort": [
 *     {
 *       "id": {
 *         "order": "desc"
 *       }
 *     }
 *   ]
 * }
 *
 * # 高亮 在有查询条件的前提下才可以使用高亮
 * GET /goods/info/_search
 * {
 *   "query": {
 *     "match": {
 *       "title": "荣耀"
 *     }
 *   },
 *   "highlight": {
 *     "fields": {
 *       "title": {
 *       }
 *     }
 *   }
 * }
 *
 * # 聚合 相当于分组
 * GET /goods/info/_search
 * {
 *   "query": {
 *     "match": {
 *       "title": "手机"
 *     }
 *   },
 *   "highlight": {
 *     "fields": {
 *       "title": {
 *       }
 *     }
 *   },
 *   "aggs": {
 *     "tmAgg": {
 *       "terms": {
 *         "field": "tmId",
 *         "size": 10
 *       }
 *     }
 *   }
 * }
 *
 * # Nested
 * DELETE /my_index
 *
 * GET /my_index/_search
 *
 * # 步骤1：建立一个index
 * PUT my_index/_doc/1
 * {
 *   "group" : "fans",
 *   "user" : [
 *     {
 *       "first" : "John",
 *       "last" :  "Smith"
 *     },
 *     {
 *       "first" : "Alice",
 *       "last" :  "White"
 *     }
 *   ]
 * }
 *
 * # 步骤2 : 执行查询
 * GET my_index/_search
 * {
 *   "query": {
 *     "bool": {
 *       "must": [
 *         { "match": { "user.first": "Alice" }},
 *         { "match": { "user.last":  "Smith" }}
 *       ]
 *     }
 *   }
 * }
 *
 *
 * # 步骤3：删除当前索引
 * DELETE /my_index
 *
 * # 步骤4：建立一个nested 类型的  就是一个整体的 不可分割的一个类型
 * PUT my_index
 * {
 *   "mappings": {
 *     "_doc": {
 *       "properties": {
 *         "user": {
 *           "type": "nested"
 *         }
 *       }
 *     }
 *   }
 * }
 *
 * GET /my_index/_mapping
 *
 * # 重新执行步骤1，使用nested 查询
 * GET /my_index/_search
 * {
 *   "query": {
 *     "nested": {
 *       "path": "user",
 *       "query": {
 *         "bool": {
 *           "must": [
 *             {"match": {"user.first": "Alice"}},
 *             {"match": {"user.last": "Smith"}}
 *           ]
 *         }
 *       }
 *     }
 *   }
 * }
 *
 *
 * @Author: yihaosun
 * @Date: 2021/10/11 13:18
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan({"com.matrix.gmall"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.matrix.gmall"})
public class ServiceListApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceListApplication.class, args);
    }
}
