package com.matrix.demothymeleaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: yihaosun
 * @Date: 2021/9/13 21:34
 */
@Controller
public class IndexController {
    // 编写控制器
    @GetMapping("index")
    public String index(HttpServletRequest request, HttpSession session) {
        request.setAttribute("msg", "Hello...");

        // 存一个集合数据
        List<String> list = Arrays.asList("syh", "ly");
        request.setAttribute("slist", list);

        // 存一个年龄
        request.setAttribute("age", 18);

        // session存储
        session.setAttribute("sname", "sunyihao");

        // 返回视图名称 默认的渲染路径是从resources/templates下面找视图名;
        return "index";
    }
}
