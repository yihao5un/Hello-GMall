package com.matrix.gmall.product.controller;

import com.matrix.gmall.product.service.TestService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: yihaosun
 * @Date: 2021/9/23 20:44
 */
@Api(tags = "测试接口")
@RestController
@RequestMapping("admin/product/test")
public class TestController {
    @Autowired
    private TestService testService;

    @GetMapping("testLock")
    public void testLock() {
        testService.testLock();
    }
}
