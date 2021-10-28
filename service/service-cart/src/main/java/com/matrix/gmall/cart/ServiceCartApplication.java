package com.matrix.gmall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @Author: yihaosun
 * @Date: 2021/10/28 20:53
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.matrix.gmall")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.matrix.gmall")
@EnableAsync
public class ServiceCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCartApplication.class,args);
    }
}
