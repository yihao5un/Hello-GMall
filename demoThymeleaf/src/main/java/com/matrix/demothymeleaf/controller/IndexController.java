package com.matrix.demothymeleaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: yihaosun
 * @Date: 2021/9/13 21:34
 */
@Controller
public class IndexController {
    // 编写控制器
    @GetMapping("index")
    public String index(HttpServletRequest request) {
        request.setAttribute("msg", "Hello...");
        // 返回视图名称 默认的渲染路径是从resources/templates下面找视图名;
        return "index";
    }
}
