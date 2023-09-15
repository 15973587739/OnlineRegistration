package com.atguigu.yygh.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication //注解用于标识该类是一个Spring Boot应用程序的主类。
public class ServerGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerGatewayApplication.class, args); //启动Spring Boot应用程序。
    }
}
