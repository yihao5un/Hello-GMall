package com.matrix.gmall.mq.controller;

import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.common.service.RabbitService;
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

    @GetMapping("sendConfirm")
    public Result<String> sendConfirm() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 指定了exchange和routing 并没有指定队列 在消费端需要用 bindings
        rabbitService.sendMessage("exchange.confirm", "routing.confirm", sdf.format(new Date()));
        return Result.ok();
    }
}
