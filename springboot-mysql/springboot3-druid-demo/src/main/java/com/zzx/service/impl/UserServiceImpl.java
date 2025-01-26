package com.zzx.service.impl;


import com.zzx.entity.User;
import com.zzx.mapper.UserMapper;
import com.zzx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> queryAllUserInfo() {
        return userMapper.queryAllUserInfo();
    }
}
