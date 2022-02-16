package com.matrix.gmall.activity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author yihaosun
 * @date 2022/2/16 21:10
 */
@SpringBootApplication
@ComponentScan({"com.matrix.gmall"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.matrix.gmall"})
public class ServiceActivityApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceActivityApplication.class, args);
    }
}