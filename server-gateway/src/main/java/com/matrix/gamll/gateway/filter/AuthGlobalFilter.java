package com.matrix.gamll.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.common.result.ResultCodeEnum;
import com.matrix.gmall.common.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Author: yihaosun
 * @Date: 2021/10/27 20:39
 */
//@Configuration
@Component
public class AuthGlobalFilter implements GlobalFilter {
    // 从配置文件中获取到都有谁
    @Value("${authUrls.url}")
    private String authUrlsUrl;

    // 引用个对象 用于做URL匹配的
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 需要知道用户访问的URL 是谁
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        // 一. 判断当前的path是哪一种? /**/inner/* 这种是内部接口 需要作出相应 提示没有权限
        if (antPathMatcher.match("/**/inner/**", path)) {
            // 获取到相应的返回对象
            ServerHttpResponse response = exchange.getResponse();
            // 不能继续走了
            return out(response, ResultCodeEnum.PERMISSION);
        }

        // 获取用户Id 那么就是登陆了 如果没有就没有登陆
        String userId = this.getUserId(request);
        if ("-1".equals(userId)) {
            // 被盗用了
            ServerHttpResponse response = exchange.getResponse();
            return out(response, ResultCodeEnum.PERMISSION);
        }

        // 二. 判断用户是否访问了 trade.html,myOrder.html 等控制器的时候 必须要登陆
        String[] split = authUrlsUrl.split(",");
        for (String url : split) {
            // 表示当前path包含上述控制器地址
            // 用户未登陆 并且访问的控制器是需要用户登陆的
            if (path.indexOf(url) != -1 && StringUtils.isEmpty(userId)) {
                // 跳转到登陆页面
                // 拿到相应对象
                ServerHttpResponse response = exchange.getResponse();
                // 做一些设置
                // 303状态码表示由于请求对应的资源存在着另一个URI，应使用重定向获取请求的资源 做重定向来获取资源
                response.setStatusCode(HttpStatus.SEE_OTHER);
                response.getHeaders().set(HttpHeaders.LOCATION, "http://www.gmall.com/login.html?originUrl=" + request.getURI());
                // 真正的重定向 ！
                return response.setComplete();
            }
        }

        // 三. 判断 /api/**/auth/** 需要登陆的
        if (antPathMatcher.match("/api/**/auth/**", path)) {
            // 判断当前是否登陆
            if (StringUtils.isEmpty(userId)) {
                // 作出相应
                ServerHttpResponse response = exchange.getResponse();
                // 未登陆 要弹出消息提示
                return out(response, ResultCodeEnum.LOGIN_AUTH);
            }
        }

        // 验证通过之后: 将用户的Id传递给后台微服务
        if (!StringUtils.isEmpty(userId)) {
            // 需要将用户Id 放入请求头中
            request.mutate().header("userId", userId).build();
            return chain.filter(exchange.mutate().request(request).build());
        }
        // 默认返回
        return chain.filter(exchange);
    }

    /**
     * 返回响应
     *
     * @param response       response
     * @param resultCodeEnum resultCodeEnum
     * @return Mono<Void>
     */
    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum resultCodeEnum) {
        // 提示的数据: resultCodeEnum 中的 getMessage数据
        Result result = Result.build(null, resultCodeEnum);
        // 需要将 result 输出到页面上 将 result变成 Json 字符串
        String strJson = JSON.toJSONString(result);
        DataBuffer wrap = response.bufferFactory().wrap(strJson.getBytes());
        // 使用相应对象设置相应头
        response.getHeaders().add("Content-type",
                "application/json;charset=UTF-8");
        // wrapper 输出出去
        response.writeWith(Mono.just(wrap));
        return null;
    }

    /**
     * 获取用户ID
     *
     * @param request request
     * @return String
     */
    private String getUserId(ServerHttpRequest request) {
        String token = "";
        // 用户Id 存储在缓存中 userKey = user:login:token
        List<String> list = request.getHeaders().get("token");
        if (!CollectionUtils.isEmpty(list)) {
            // 拿到 token 这个字段
            token = list.get(0);
        } else {
            // 如果为空 那么从cookie中去获取
            HttpCookie httpCookie = request.getCookies().getFirst("token");
            if (httpCookie != null) {
                token = httpCookie.getValue();
            }
        }
        // 组成缓存的Key
        String userKey = "user:login" + token;
        // 可以看作是字符串
        String object = (String) redisTemplate.opsForValue().get(userKey);
        if (!StringUtils.isEmpty(object)) {
            // 再转一下 从字符串转为Json
            JSONObject jsonObject = JSON.parseObject(object, JSONObject.class);
            // 校验 先获取到当前客户端的Ip
            String curIp = IpUtil.getGatwayIpAddress(request);
            String ip = (String) jsonObject.get("ip");
            if (curIp.equals(ip)) {
                String userId = (String) jsonObject.get("userId");
                return userId;
            } else {
                // 不是的话 userId 返回 -1
                return "-1";
            }
        } else {
            return "";
        }
    }
}
