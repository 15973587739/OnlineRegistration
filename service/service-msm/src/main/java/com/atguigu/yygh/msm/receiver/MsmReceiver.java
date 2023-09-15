package com.atguigu.yygh.msm.receiver;

import com.atguigu.common.rabbit.constant.MqConst;
import com.atguigu.yygh.msm.service.MsmService;
import com.atguigu.yygh.vo.msm.MsmVo;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;

/**
 * 短信接收器
 *
 * 该类用于接收短信消息并进行处理。
 * @author SIYU
 */
@Component // 使用Spring注解，将该类标记为一个组件，供Spring容器管理。
public class MsmReceiver {

    @Autowired
    private MsmService msmService;

    /**
     * 发送短信
     *
     * 如果队列有信息就进行发送短信，实现了队列订阅。
     *
     * @param msmVo   短信信息
     * @param message RabbitMQ消息对象
     * @param channel RabbitMQ通道对象
     * @throws IOException 发送短信时可能抛出的IO异常
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_MSM_ITEM, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_MSM),
            key = {MqConst.ROUTING_MSM_ITEM}
    ))
    public void send(MsmVo msmVo, Message message, Channel channel) throws IOException {
        msmService.send(msmVo); //该方法用于发送短信，通过RabbitMQ的消息监听器@RabbitListener实现队列订阅。当队列中有消息时，该方法会被调用，并将消息转发给msmService对象进行发送。
    }

}

