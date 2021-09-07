package com.matrix.gamll.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;


@Configuration
public class CorsConfig {
    /**
     * CorsConfig 配置类
     * 相当于将当前的类变成xxx.xml
     *
     * Cors跨域配置对象
     * @Bean 相当于创建一个bean 对象交给Spring容器管理
     * <bean class = "org.springframework.web.cors.reactive.CorsWebFilter"></>
     * @return void
     */
    @Bean
    public CorsWebFilter corsWebFilter(){
        CorsConfiguration configuration = new CorsConfiguration();
        // 请求域 允许任何的域名
        configuration.addAllowedOrigin("*");
        // 是否允许携带Cookie信息
        configuration.setAllowCredentials(true);
        // 请求方法 GET POST PUT
        configuration.addAllowedMethod("*");
        // 请求头 允许任何请求头
        configuration.addAllowedHeader("*");
        // 配置源对象
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**", configuration);
        // cors过滤器对象
        return new CorsWebFilter(configurationSource);
    }
}
