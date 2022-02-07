package com.matrix.gmall.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * AliPay Module
 *
 * @author yihaosun
 * @date 2022/2/7 14:29
 */
@SpringBootApplication
@ComponentScan({"com.matrix.gmall"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.matrix.gmall"})
public class ServicePaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServicePaymentApplication.class, args);
    }
}
