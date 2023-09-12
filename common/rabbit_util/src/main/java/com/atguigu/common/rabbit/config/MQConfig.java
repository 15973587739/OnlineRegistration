package com.atguigu.common.rabbit.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQ配置类
 * @author SIYU
 */
@Configuration //标记类为配置类
public class MQConfig {

    /**
     * 配置消息转换器
     *
     * @return Jackson2JsonMessageConverter 消息转换器实例
     */
    @Bean //用于将方法返回的对象注册为 Spring 容器中的 Bean。
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
