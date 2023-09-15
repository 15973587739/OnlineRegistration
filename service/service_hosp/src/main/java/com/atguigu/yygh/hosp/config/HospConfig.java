package com.atguigu.yygh.hosp.config;


import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author SIYU
 */
@Configuration //标识该类为配置类，Spring 在启动时会加载并解析该类。
@MapperScan("com.atguigu.yygh.hosp.mapper") //指定了需要扫描的包路径，用于自动扫描并注册 Mapper 接口的实现。
public class HospConfig {
    /**
     * 分页插件
     */
    @Bean //将方法返回的对象注册为一个 Bean。
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }
}
