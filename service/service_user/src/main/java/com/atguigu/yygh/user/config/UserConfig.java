package com.atguigu.yygh.user.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author SIYU
 * 该类实现了对MyBatis的Mapper接口进行扫描，并配置了一个分页插件
 */
@Configuration //是一个Spring注解，用于标识该类是一个配置类。这意味着该类将包含一些配置信息和Bean定义。
@MapperScan("com.atguigu.yygh.user.mapper") //是一个MyBatis注解，用于指定要扫描的Mapper接口所在的包路径。在这里，它指示MyBatis将扫描com.atguigu.yygh.user.mapper包下的Mapper接口。
public class UserConfig {
    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }
}
