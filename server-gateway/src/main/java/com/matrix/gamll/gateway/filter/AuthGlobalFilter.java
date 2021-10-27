package com.matrix.gamll.gateway.filter;

import com.matrix.gmall.common.result.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author: yihaosun
 * @Date: 2021/10/27 20:39
 */
public class AuthGlobalFilter implements GlobalFilter {
    // 从配置文件中获取到都有谁
    @Value("${authUrls.url}")
    private String authUrlsUrl;

    // 引用个对象
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 需要知道用户访问的URL 是谁
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        // 判断当前的path是哪一种? /**/inner/* 这种是内部接口 需要作出相应 提示没有权限
        if (antPathMatcher.match("/**/inner/**", path)) {
            // 获取到相应的返回对象
            ServerHttpResponse response = exchange.getResponse();
            // 不能继续走了
            //TODO return out(response, ResultCodeEnum.PERMISSION);
        }

        // 获取用户Id 那么就是登陆了 如果没有就没有登陆
        String userId = this.getUserId(request);

        // 判断用户是否访问了 trade.html,myOrder.html 等控制器的时候 必须要登陆
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
            }
        }

        return null;
    }

    /**
     * 获取用户ID
     * @param request request
     * @return String
     */
    private String getUserId(ServerHttpRequest request) {
        return null;
    }
}
