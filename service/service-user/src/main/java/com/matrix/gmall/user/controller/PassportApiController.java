package com.matrix.gmall.user.controller;

import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.model.user.UserInfo;
import com.matrix.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;

/**
 * @Author: yihaosun
 * @Date: 2021/10/25 22:26
 */
@RestController
@RequestMapping("/api/user/passport")
public class PassportApiController {
    @Autowired
    private UserService userService;

    @PostMapping("login")
    public Result login(@RequestBody UserInfo userInfo) {
        /**
         * 1. 登陆成功后需要生成token 然后将token放入cookie中 和 userInfo对象
         *  Cookie cookie = new Cookie("name", "111");
         * 2. 设置作用域 只有在这个作用域下 才可以得到key的值
         *  cookie.setDomain("gmall.com");
         * 3. 设置项目的跟路径
         *  cookie.setPath("/");
         * 4. 设置过期时间
         *  cookie.setMaxAge(7 * 24 * 60 * 60);
         */
        UserInfo login = userService.login(userInfo);
        return Result.ok();
    }
}
