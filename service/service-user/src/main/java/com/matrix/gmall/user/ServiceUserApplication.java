package com.matrix.gmall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: yihaosun
 * @Date: 2021/10/25 22:05
 */
@SpringBootApplication
@ComponentScan({"com.matrix.gmall"})
@EnableDiscoveryClient
public class ServiceUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication.class);
    }
}
