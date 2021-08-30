package com.matrix.gmall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: yihaosun
 * @Date: 2021/8/30 22:33
 */
@SpringBootApplication
@ComponentScan({"com.matrix.gmall"})
@EnableDiscoveryClient
public class ServiceProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceProductApplication.class, args);
    }
}
