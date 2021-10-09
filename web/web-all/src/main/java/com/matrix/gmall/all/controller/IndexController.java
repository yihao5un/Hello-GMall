package com.matrix.gmall.all.controller;

import com.alibaba.fastjson.JSONObject;
import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.product.client.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author: yihaosun
 * @Date: 2021/10/9 16:05
 */
@Controller
public class IndexController {
    @Autowired
    private ProductFeignClient productFeignClient;

    /**
     * http://www.gmall.com/index.html 添加一个数组 "/" "html"
     * @param request request
     * @return Result<List<JSONObject>>
     */
    @GetMapping({"/", "index.html"})
    public String index(HttpServletRequest request) {
        Result<List<JSONObject>> result = productFeignClient.getBaseCategoryList();
        // 存储一个list ${list} 所以声明的attribute叫做list
        request.setAttribute("list", result.getData());
        // 返回index目录下面的index.html
        return "index/index";
    }
}
