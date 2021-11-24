package com.matrix.gmall.user.controller;

import com.matrix.gmall.model.user.UserAddress;
import com.matrix.gmall.user.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: yihaosun
 * @Date: 2021/11/24 20:43
 */
@RestController
@RequestMapping("/api/user")
public class UserApiController {
    @Autowired
    private UserAddressService userAddressService;

    @GetMapping("inner/findUserAddressListByUserId/{userId}")
    public List<UserAddress> findUserAddressListByUserId(@PathVariable Long userId) {
        return userAddressService.findUserAddressListByUserId(userId);
    }
}
