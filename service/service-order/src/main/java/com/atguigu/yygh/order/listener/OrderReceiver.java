package com.atguigu.yygh.order.listener;

import com.atguigu.common.rabbit.constant.MqConst;
import com.atguigu.yygh.order.service.OrderService;
import com.google.protobuf.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.channels.Channel;

/**
 * 消息监听器
 * @author SIYU
 */
@Component
public class OrderReceiver {

    @Autowired
    private OrderService orderService;

    /**
     * 患者提醒消息监听器
     *
     * @param message 消息对象
     * @param channel RabbitMQ通道
     * @throws IOException 如果发生I/O错误
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_TASK_8, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_TASK),
            key = {MqConst.ROUTING_TASK_8}
    ))
    public void patientTips(Message message, Channel channel) throws IOException {
        orderService.patientTips();
    }
}
