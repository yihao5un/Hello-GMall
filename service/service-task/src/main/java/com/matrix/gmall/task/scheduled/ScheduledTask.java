package com.matrix.gmall.task.scheduled;

import com.matrix.gmall.common.constant.MqConst;
import com.matrix.gmall.common.service.RabbitService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务
 *
 * @author yihaosun
 * @date 2022/2/16 21:21
 */
@Component
@EnableScheduling
public class ScheduledTask {
    private static final Log logger = LogFactory.getLog(ScheduledTask.class);

    @Autowired
    private RabbitService rabbitService;

    /**
     * corn表达式: 秒 分 时 日 月 周 (年)
     * 0/10 * * * * ? 每隔10s执行一下
     * 0/30 * * * * ? 每隔30s执行一下
     *
     * 0 0 1 * * ? 每天凌晨1点执行代码
     * {@link com.matrix.gmall.activity.receiver.SecKillReceiver#importToRedis()}
     */
    @Scheduled(cron = "* * * * * ?")
    public void sendMsg() {
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK, MqConst.ROUTING_TASK_1, "send...");
    }

    @Scheduled(cron = "0 0 18 * * ?")
    public void clearRedis() {
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK, MqConst.ROUTING_TASK_18, "del...");
    }
}
