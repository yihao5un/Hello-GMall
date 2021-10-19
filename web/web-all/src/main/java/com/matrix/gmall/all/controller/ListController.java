package com.matrix.gmall.all.controller;

import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.list.client.ListFeignClient;
import com.matrix.gmall.model.list.SearchParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: yihaosun
 * @Date: 2021/10/14 20:36
 */
@Controller
public class ListController {
    @Qualifier("com.matrix.gmall.list.client.ListFeignClient")
    @Autowired
    private ListFeignClient listFeignClient;

    @ApiOperation("列表搜索")
    @GetMapping("list.html")
    public String search(SearchParam searchParam, Model model) {
        Result<Map> result = listFeignClient.list(searchParam);
        model.addAllAttributes(result.getData());

        // 记录拼接url
        String urlParam = makeUrlParam(searchParam);
        // 处理品牌条件回显
        String trademarkParam = makeTrademark(searchParam.getTrademark());
        // 处理平台属性条件回显
        List<Map<String, String>> maps = makeProps(searchParam.getProps());
        // 处理排序
        Map<String, Object> orderMap = dealOrder(searchParam.getOrder());

        model.addAttribute("searchParam", searchParam);
        model.addAttribute("urlParam", urlParam);
        model.addAttribute("trademarkParam", trademarkParam);
        model.addAttribute("propsParamList", maps);
        model.addAttribute("orderMap", orderMap);
        return "list/index";
    }

    /**
     * 获取到请求路径中的请求参数 制作返回的url
     *
     * @param searchParam searchParam
     * @return String
     */
    private String makeUrlParam(SearchParam searchParam) {
        StringBuilder urlParam = new StringBuilder();
        // 1. 判断是否通过关键字进行检索
        if (searchParam.getKeyword() != null) {
            urlParam.append("keyword=").append(searchParam.getKeyword());
        }
        // 2. 通过分类去检索
        if (searchParam.getCategory1Id() != null) {
            urlParam.append("category1Id=").append(searchParam.getCategory1Id());
        }
        if (searchParam.getCategory2Id() != null) {
            urlParam.append("category2Id=").append(searchParam.getCategory2Id());
        }
        if (searchParam.getCategory3Id() != null) {
            urlParam.append("category3Id=").append(searchParam.getCategory3Id());
        }
        // 3. 通过品牌属性检索
        if (searchParam.getTrademark() != null && urlParam.length() > 0) {
            urlParam.append("&trademark=").append(searchParam.getTrademark());
        }
        // 4. 通过属性检索
        if (searchParam.getProps() != null && urlParam.length() > 0) {
            for (String prop : searchParam.getProps()) {
                if (urlParam.length() > 0) {
                    urlParam.append("&props=").append(prop);
                }
            }
        }
        //urlParam是StringBuilder类型
        return "list.html?" + urlParam;
    }

    /**
     * 处理品牌条件回显
     *
     * @param trademark trademark
     * @return String
     */
    private String makeTrademark(String trademark) {
        if (!StringUtils.isEmpty(trademark)) {
            String[] split = StringUtils.split(trademark, ":");
            if (split != null && split.length == 2) {
                return "品牌:" + split[1];
            }
        }
        return "";
    }

    /**
     * 处理平台属性条件回显
     *
     * @param props props
     * @return List<Map < String, String>>
     */
    private List<Map<String, String>> makeProps(String[] props) {
        List<Map<String, String>> list = new ArrayList<>();
        //2:v:n
        if (props != null && props.length != 0) {
            for (String prop : props) {
                //prop = 2:6.25-6.34英寸:屏幕尺寸
                //prop = 4:64GB:机身存储
                String[] split = StringUtils.split(prop, ":");
                if (split != null && split.length == 3) {
                    //声明一个Map
                    HashMap<String, String> map = new HashMap<>();
                    map.put("attrId", split[0]);
                    map.put("attrValue", split[1]);
                    map.put("attrName", split[2]);
                    list.add(map);
                }
            }
        }
        return list;
    }

    /**
     * 处理排序
     *
     * @param order order
     * @return Map<String, Object>
     */
    private Map<String, Object> dealOrder(String order) {
        Map<String, Object> objectMap = new HashMap<>();
        if (!StringUtils.isEmpty(order)) {
            String[] split = StringUtils.split(order, ":");
            if (split != null && split.length == 2) {
                //传递的哪个字段
                objectMap.put("type", split[0]);
                //升序降序
                objectMap.put("sort", split[1]);
            }
        } else {
            objectMap.put("type", "1");
            objectMap.put("sort", "asc");
        }
        return objectMap;
    }
}

