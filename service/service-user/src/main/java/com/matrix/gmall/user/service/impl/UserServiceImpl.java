package com.matrix.gmall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.matrix.gmall.model.user.UserInfo;
import com.matrix.gmall.user.mapper.UserInfoMapper;
import com.matrix.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Objects;

/**
 * @Author: yihaosun
 * @Date: 2021/10/25 22:15
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfo login(UserInfo userInfo) {
        UserInfo info = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getLoginName, userInfo.getLoginName())
                // 注意: 密码需要加密 MD5
                .eq(UserInfo::getPasswd, DigestUtils.md5DigestAsHex(userInfo.getPasswd().getBytes())));
        if (Objects.nonNull(info)) {
            return info;
        }
        return null;
    }
}
