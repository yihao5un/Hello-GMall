package com.matrix.gamll.gateway.config;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * CorsConfig 配置类
 * 想当于将当前的类变成xxx.xml
 *
 * @Author: yihaosun
 * @Date: 2021/9/5 17:33
 */
@CacheConfig
public class CorsConfig {
    /**
     * @Bean 相当于创建一个bean 对象交给Spring容器管理
     * <bean class = "org.springframework.web.cors.reactive.CorsWebFilter"></>
     * @return void
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 请求头 允许任何请求头
        corsConfiguration.addAllowedHeader("*");
        // 请求域 允许任何的域名
        corsConfiguration.addAllowedOrigin("*");
        // 请求方法 GET POST PUT
        corsConfiguration.addAllowedMethod("*");
        // 是否允许携带Cookie信息
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(urlBasedCorsConfigurationSource);
    }
}
