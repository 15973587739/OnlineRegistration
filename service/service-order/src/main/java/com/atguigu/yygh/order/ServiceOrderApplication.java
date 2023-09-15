package com.atguigu.yygh.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


/**
 * @author SIYU
 */
@SpringBootApplication //是订单服务的启动类。
@ComponentScan(basePackages = {"com.atguigu"}) //注解指定了需要扫描的基础包路径，这里包括了"com.atguigu"包及其子包下的组件。
@EnableDiscoveryClient //注解启用服务注册与发现，以便与服务发现组件集成。
@EnableFeignClients(basePackages = {"com.atguigu"}) //注解开启Feign客户端的功能，指定了"com.atguigu"包及其子包下的Feign客户端接口。
public class ServiceOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceOrderApplication.class, args);
    }
}
