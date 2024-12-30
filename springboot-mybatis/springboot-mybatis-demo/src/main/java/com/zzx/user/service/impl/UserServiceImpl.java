package com.zzx.user.service.impl;


import com.zzx.user.repository.entity.User;
import com.zzx.user.repository.mapper.UserMapper;
import com.zzx.user.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;

    @Override
    public List<User> queryAllUserInfo() {
        return userMapper.queryAllUserInfo();
    }
}
