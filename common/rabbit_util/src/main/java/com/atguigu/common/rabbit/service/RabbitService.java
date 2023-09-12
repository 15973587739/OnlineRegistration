package com.atguigu.common.rabbit.service;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 这是一个 RabbitMQ 服务类 RabbitService，用于发送消息。
 * @author SIYU
 */
@Service //用于标记类为服务类，表示该类提供业务逻辑的服务。
public class RabbitService {

    @Autowired //用于自动装配 RabbitTemplate 对象，实现对 RabbitMQ 操作的依赖注入。
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     *
     * @code 发送消息的方法。通过指定的交换机、路由键和消息内容，使用 rabbitTemplate 发送消息到 RabbitMQ。
     * @param exchange   交换机
     * @param routingKey 路由键
     * @param message    消息
     * @return 发送消息是否成功的布尔值
     */
    public boolean sendMessage(String exchange, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        return true;
    }
}
