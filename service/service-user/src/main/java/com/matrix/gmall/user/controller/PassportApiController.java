package com.matrix.gmall.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.matrix.gmall.common.constant.RedisConst;
import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.common.util.IpUtil;
import com.matrix.gmall.model.user.UserInfo;
import com.matrix.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: yihaosun
 * @Date: 2021/10/25 22:26
 */
@RestController
@RequestMapping("/api/user/passport")
public class PassportApiController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("login")
    public Result login(@RequestBody UserInfo userInfo, HttpServletRequest request) {
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
        UserInfo info = userService.login(userInfo);
        if (Objects.nonNull(info)) {
            String token = UUID.randomUUID().toString();
            // 用户登录成功
            // 判断用户是否登陆的真实依据, 应该是将数据放入缓存
            // 确定数据类型以及Key = user:login:token
            String userKey = RedisConst.USER_LOGIN_KEY_PREFIX + token;
            // 缓存中到底该存储什么？ 可以只存用户Id
            // 为了防止伪造token做登陆 此时 需要存储一个IP地址！！！
            // 获取到当前服务器的Ip地址
            String ip = IpUtil.getIpAddress(request);
            // 定义一个JSONObject对象
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", info.getId().toString());
            // 防止token被盗用 伪造token的
            jsonObject.put("ip", ip);
            // 用JSONObject当作Value
            redisTemplate.opsForValue().set(userKey,
                    jsonObject.toJSONString(),
                    RedisConst.USERKEY_TIMEOUT, TimeUnit.SECONDS);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("token", token);
            hashMap.put("nickname", info.getNickName());
            return Result.ok(hashMap);
        } else {
            return Result.fail().message("登陆失败!");
        }
    }
}
