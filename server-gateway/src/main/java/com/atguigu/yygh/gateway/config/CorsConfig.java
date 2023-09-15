package com.atguigu.yygh.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * 跨域配置类
 * @author SIYU
 */
@Configuration //注解表示这是一个配置类
public class CorsConfig {
    /**
     * 这段代码的配置，可以实现跨域请求的处理，允许来自任何来源的请求访问该应用程序，并允许使用任何HTTP方法和请求头。
     */
    @Bean
    public CorsWebFilter corsFilter() {
        // 创建CorsConfiguration对象，并进行跨域配置
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*"); // 允许所有HTTP方法
        config.addAllowedOrigin("*"); // 允许所有来源
        config.addAllowedHeader("*"); // 允许所有请求头

        // 创建UrlBasedCorsConfigurationSource对象，并注册CorsConfiguration
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);

        // 返回CorsWebFilter对象
        return new CorsWebFilter(source);
    }
}

