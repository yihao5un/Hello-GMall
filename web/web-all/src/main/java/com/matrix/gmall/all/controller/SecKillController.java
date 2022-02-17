package com.matrix.gmall.all.controller;

import com.matrix.gmall.activity.client.ActivityFeignClient;
import com.matrix.gmall.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author yihaosun
 * @date 2022/2/17 19:58
 */
@Controller
public class SecKillController {
    @Autowired
    private ActivityFeignClient activityFeignClient;

    @GetMapping("seckill.html")
    public String seckill(Model model) {
        Result<Object> result = activityFeignClient.findAll();
        // 后台存储一个List
        model.addAttribute("list", result.getData());
        // 返回视图
        return "seckill/index";
    }

    @GetMapping("seckill/{skuId}.html")
    public String seckillItem(@PathVariable Long skuId, Model model) {
        Result<Object> result = activityFeignClient.getSeckillGoods(skuId);
        model.addAttribute("item", result.getData());
        return "seckill/item";
    }

    @GetMapping("seckill/queue.html")
    public String queue(HttpServletRequest request) {
        String skuId = request.getParameter("skuId");
        String skuIdStr = request.getParameter("skuIdStr");
        request.setAttribute("skuId", skuId);
        request.setAttribute("skuIdStr", skuIdStr);
        return "seckill/queue";
    }

    @GetMapping("seckill/trade.html")
    public String trade(Model model) {
        //  后台需要存储 detailArrayList， userAddressList ，totalNum，totalAmount
        //  远程调用秒杀的feignClient
        Result<Map<String, Object>> result = activityFeignClient.trade();
        //  数据正常
        if (result.isOk()) {
            //  保存数据
            model.addAllAttributes(result.getData());
            //  返回视图名称
            return "seckill/trade";
        } else {
            //  保存数据
            model.addAttribute("message", "下单失败");
            //  返回视图名称
            return "seckill/fail";
        }
    }
}
