package com.atguigu.yygh.hosp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author SIYU
 * 用于启动医院服务
 */
@SpringBootApplication //Spring Boot 应用程序注解，表示该类是一个 Spring Boot 应用程序的入口。
@ComponentScan(basePackages = "com.atguigu") //指定组件扫描的基础包路径，这里是 "com.atguigu"。
@EnableDiscoveryClient  //启用服务发现功能。
@EnableFeignClients(basePackages = "com.atguigu") //启用 Feign 客户端，并指定基础包路径，这里是 "com.atguigu"。
public class ServiceHospApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospApplication.class, args);
    }
}
