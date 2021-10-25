package com.matrix.gmall.user.service;

import com.matrix.gmall.model.user.UserInfo;

/**
 * @Author: yihaosun
 * @Date: 2021/10/25 22:14
 */
public interface UserService {
    /**
     * 用户登陆
     * @param userInfo userInfo
     * @return UserInfo
     */
    UserInfo login(UserInfo userInfo);
}
