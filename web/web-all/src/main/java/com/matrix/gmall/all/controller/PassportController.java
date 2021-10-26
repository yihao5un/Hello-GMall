package com.matrix.gmall.all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: yihaosun
 * @Date: 2021/10/26 22:39
 */
@Controller
public class PassportController {
    @RequestMapping("login.html")
    public String loginIndex(HttpServletRequest request) {
        // 获取到路径
        String originUrl = request.getParameter("originUrl");
        // 结合登陆流程 保存到作用域中
        request.setAttribute("originUrl", originUrl);
        return "login";
    }
}
