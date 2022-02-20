package com.matrix.gmall.activity.receiver;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.matrix.gmall.activity.mapper.SecKillGoodsMapper;
import com.matrix.gmall.activity.service.SecKillGoodsService;
import com.matrix.gmall.common.constant.MqConst;
import com.matrix.gmall.common.constant.RedisConst;
import com.matrix.gmall.common.util.DateUtil;
import com.matrix.gmall.model.activity.SeckillGoods;
import com.matrix.gmall.model.activity.UserRecorde;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * 秒杀监听队列
 *
 * @author yihaosun
 * @date 2022/2/17 14:16
 */
@Component
public class SecKillReceiver {
    @Autowired
    private SecKillGoodsMapper secKillGoodsMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private SecKillGoodsService secKillGoodsService;

    /**
     * 监听消息 将秒杀商品数据放入缓存
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_TASK_1, durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_TASK),
            key = {MqConst.ROUTING_TASK_1}))
    @SneakyThrows
    public void importToRedis(Message message, Channel channel) {
        // 查询当天的秒杀商品集合
        List<SeckillGoods> seckillGoodsList = secKillGoodsMapper.selectList(new LambdaQueryWrapper<SeckillGoods>()
                .eq(SeckillGoods::getStatus, 1)
                .gt(SeckillGoods::getStockCount, 0)
                // 数据库的年月日 和 当前系统的年月日进行比较 TODO: start_time 需要进行格式化和当前的日期进行比较
                .eq(SeckillGoods::getStartTime, DateUtil.formatDate(new Date()))
        );
        // 将集合数据放入缓存
        if (!CollectionUtils.isEmpty(seckillGoodsList)) {
            seckillGoodsList.forEach(seckillGood -> {
                String seckillKey = RedisConst.SECKILL_GOODS;
                // 判断当前秒杀商品skuId是否在缓存中存在
                if (Boolean.TRUE.equals(redisTemplate.boundHashOps(seckillKey).hasKey(seckillGood.getSkuId().toString()))) {
                    // 有数据不插入数据 继续执行 continue 在forEach中直接return即可
                    return;
                }
                // 第一种方式
                // redisTemplate.opsForHash().put(seckillKey, seckillGood.getSkuId().toString(), seckillGood);
                // 第二种方式
                redisTemplate.boundHashOps(seckillKey).put(seckillGood.getSkuId().toString(), seckillGood);
                // !!! 如何控制超卖? 将商品的数量也放入缓存(注意Redis是单线程的)
                for (int i = 0; i < seckillGood.getStockCount(); i++) {
                    String key = RedisConst.SECKILL_STOCK_PREFIX + seckillGood.getSkuId();
                    // 每卖出一个就 rightPop() 一个 为0的时候 库存就没有了
                    redisTemplate.boundListOps(key).leftPush(seckillGood.getSkuId().toString());
                }
                // 状态位初始化为1 使用redis的发布/订阅模式
                redisTemplate.convertAndSend("seckillpush", seckillGood.getSkuId() + "1");
            });
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 监听消息 预下单
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_TASK_1, durable = "true", autoDelete = "false"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_TASK),
            key = {MqConst.ROUTING_TASK_1}))
    @SneakyThrows
    public void seckill(UserRecorde userRecorde, Message message, Channel channel) {
        if (userRecorde != null) {
            secKillGoodsService.seckillOrder(userRecorde.getSkuId(), userRecorde.getUserId());
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
