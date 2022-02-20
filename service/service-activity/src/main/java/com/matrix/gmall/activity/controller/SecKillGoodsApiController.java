package com.matrix.gmall.activity.controller;

import com.matrix.gmall.activity.service.SecKillGoodsService;
import com.matrix.gmall.activity.util.CacheHelper;
import com.matrix.gmall.common.constant.MqConst;
import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.common.result.ResultCodeEnum;
import com.matrix.gmall.common.service.RabbitService;
import com.matrix.gmall.common.util.AuthContextHolder;
import com.matrix.gmall.common.util.DateUtil;
import com.matrix.gmall.common.util.MD5;
import com.matrix.gmall.model.activity.SeckillGoods;
import com.matrix.gmall.model.activity.UserRecorde;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author yihaosun
 * @date 2022/2/17 19:47
 */
@RestController
@RequestMapping("/api/activity/seckill")
public class SecKillGoodsApiController {
    @Autowired
    private SecKillGoodsService secKillGoodsService;

    @Autowired
    private RabbitService rabbitService;

    @GetMapping("findAll")
    public Result<Object> findAll() {
        return Result.ok(secKillGoodsService.findAll());
    }

    @GetMapping("getSeckillGoods/{skuId}")
    public Result<Object> getSeckillGoods(@PathVariable Long skuId) {
        return Result.ok(secKillGoodsService.findSeckillGoodsById(skuId));
    }

    /**
     * 获取下单码(用户Id MD5加密)
     * TODO: 为了防止用户非法抢购
     * /auth 这块 网关已经限制好了
     *
     * @param skuId skuId
     * @return Result
     */
    @GetMapping("/auth/getSecKillSkuStr/{skuId}")
    public Result<Object> getSecKillSkuStr(@PathVariable Long skuId, HttpServletRequest request) {
        // 根据当前的skuId 查询到秒杀商品是谁
        SeckillGoods seckillGoods = secKillGoodsService.findSeckillGoodsById(skuId);
        String userId = AuthContextHolder.getUserId(request);
        if (StringUtils.isNotEmpty(userId)) {
            Date currentTime = new Date();
            if (DateUtil.dateCompare(seckillGoods.getStartTime(), currentTime)
                    && DateUtil.dateCompare(currentTime, seckillGoods.getEndTime())) {
                // 两个都为true的话 说明在秒杀时间范围内 生成下单码
                String skuIdStr = MD5.encrypt(userId);
                // 返回下单码
                return Result.ok(skuIdStr);
            }
        }
        return Result.fail().message("获取下单码失败");
    }

    @PostMapping("/auth/seckillOrder/{skuId}")
    public Result<String> seckillOrder(@PathVariable Long skuId, HttpServletRequest request) {
        String skuIdStr = request.getParameter("skuIdStr");
        String userId = AuthContextHolder.getUserId(request);
        if (! skuIdStr.equals(MD5.encrypt(userId))) {
            // 下单码不正确
            return Result.build(null, ResultCodeEnum.SECKILL_ILLEGAL);
        }
        // 检验系统的状态位 redis订阅发布
        String state = (String) CacheHelper.get(skuId.toString());
        if (StringUtils.isEmpty(state)) {
            // 请求不合法
            return Result.build(null, ResultCodeEnum.SECKILL_ILLEGAL);
        } else if("0".equals(state)) {
            // 已售空
            return Result.build(null, ResultCodeEnum.SECKILL_FINISH);
        } else {
            //  state = 1的时候 商品可以秒杀
            UserRecorde userRecorde = new UserRecorde();
            userRecorde.setUserId(userId);
            userRecorde.setSkuId(skuId);
            // 发送到MQ
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_SECKILL_USER, MqConst.ROUTING_SECKILL_USER, userRecorde);
            return Result.ok();
        }
    }
}
