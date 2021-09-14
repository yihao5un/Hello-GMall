package com.matrix.gmall.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
 * 因为不需要连接数据库 我们的数据是从service-product模块过来的 所以要排除掉有关于数据库的配置 否则会报错的
 *
 * @Author: yihaosun
 * @Date: 2021/9/14 21:36
 */

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan({"com.matrix.gmall"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.matrix.gmall"})
public class ServiceItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceItemApplication.class, args);
    }
}
