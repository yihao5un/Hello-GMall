package com.matrix.gmall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: yihaosun
 * @Date: 2021/11/24 21:33
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.matrix.gmall")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.matrix.gmall")
public class ServiceOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceOrderApplication.class, args);
    }
}
