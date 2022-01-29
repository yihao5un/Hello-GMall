package com.matrix.gmall.mq.controller;

import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.common.service.RabbitService;
import com.matrix.gmall.mq.config.DeadLetterMqConfig;
import com.matrix.gmall.mq.config.DelayedMqConfig;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 消息发送端
 *
 * 消息接受端
 * @see com.matrix.gmall.mq.receiver.ConfirmReceiver
 *
 * @Author: yihaosun
 * @Date: 2022/1/25 16:55
 */
@RestController
@RequestMapping("/mq")
public class MqController {

    @Autowired
    private RabbitService rabbitService;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @GetMapping("sendConfirm")
    public Result<String> sendConfirm() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 指定了exchange和routing 并没有指定队列 在消费端需要用 bindings
        rabbitService.sendMessage("exchange.confirm", "routing.confirm", sdf.format(new Date()));
        return Result.ok();
    }

    @GetMapping("sendDeadLetter")
    public Result sendDeadLetter() {
        // 当前的时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("发送时间: \t" + simpleDateFormat.format(new Date()));
        rabbitService.sendMessage(DeadLetterMqConfig.EXCHANGE_DEAD, DeadLetterMqConfig.ROUTING_DEAD_1, "sendDeadLetter");
        return Result.ok();
    }

    @GetMapping("sendDelay")
    public Result sendDelay() {
        // 当前的时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 函数式接口 使用Lambda表达式
        rabbitTemplate.convertAndSend( DelayedMqConfig.EXCHANGE_DELAY, DelayedMqConfig.ROUTING_DELAY, "sendDelay", new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                // TODO 在这个地方加上延迟的时间
                message.getMessageProperties().setDelay(10000);
                System.out.println("发送时间: \t" + simpleDateFormat.format(new Date()));
                return message;
            }
        });
        return Result.ok();
    }
}
