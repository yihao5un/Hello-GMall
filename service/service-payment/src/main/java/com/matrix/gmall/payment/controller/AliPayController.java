package com.matrix.gmall.payment.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.matrix.gmall.common.result.Result;
import com.matrix.gmall.model.enums.PaymentType;
import com.matrix.gmall.model.payment.PaymentInfo;
import com.matrix.gmall.payment.config.AlipayConfig;
import com.matrix.gmall.payment.service.AlipayService;
import com.matrix.gmall.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AliPayController
 *
 * @author yihaosun
 * @date 2022/2/7 20:17
 */
@Controller
@RestController("/api/payment/alipay")
public class AliPayController {
    @Autowired
    private AlipayService alipayService;

    @Autowired
    private PaymentService paymentService;

    /**
     * 注解:@ResponseBody作用
     * 第一个：返回数据是Json
     * 第二个：直接将数据输入到页面！
     *
     * @param orderId orderId
     * @return String
     */
    @RequestMapping("submit/{orderId}")
    @ResponseBody
    public String aliPay(@PathVariable("orderId") Long orderId) throws AlipayApiException {
        return alipayService.createAliPay(orderId);
    }

    /**
     * 同步回调(用户)
     * 重定向到 return_order_url 这个地址 (web-all中)
     * {@link com.matrix.gmall.all.controller.PaymentController}
     */
    @GetMapping("callback/return")
    public String callbackReturn() {
        return "redirect:" + AlipayConfig.return_order_url;
    }

    /**
     * 异步回调(商家)
     * 需要内网穿透
     */
    @PostMapping("callback/notify")
    @ResponseBody
    public String callbackNotify(@RequestParam Map<String, String> paramMap) throws AlipayApiException {
        // 通过key获取到对应的数据
        String outTradeNo = paramMap.get("out_trade_no");
        // outTradeNo 与 商家的outTradeNo
        // 如果通过这个outTradeNo 在交易记录中能够获取到数据，则说明验证成功！
        PaymentInfo paymentInfo = paymentService.getPaymentInfo(outTradeNo, PaymentType.ALIPAY.name());
        // 说明验证失败！outTradeNo
        String status = paramMap.get("trade_status");
        // 验证总金额
        // 调用SDK验证签名
        boolean flag = AlipaySignature.rsaCheckV1(paramMap, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);
        if (flag) {
            // 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
            // 校验各种参数是否正确！
            if ("TRADE_SUCCESS".equals(status) || "TRADE_FINISHED".equals(status)) {
                //  细节： 防止万一 {当交易状态是支付完成，或者交易结束时} 支付状态是CLOSED 或者是PAID 则返回failure！
                if ("PAID".equals(paymentInfo.getPaymentStatus()) || "ClOSED".equals(paymentInfo.getPaymentStatus())) {
                    return "failure";
                }
                //  更新交易记录状态！
                paymentService.paySuccess(outTradeNo, PaymentType.ALIPAY.name(), paramMap);
                return "success";
            }
        } else {
            // 验签失败则记录异常日志，并在response中返回failure.
            return "failure";
        }
        return "false";
    }

    @GetMapping("refund/{orderId}")
    @ResponseBody
    public Result<Boolean> refund(@PathVariable Long orderId) {
        boolean flag = alipayService.refund(orderId);
        return Result.ok(flag);
    }
}
