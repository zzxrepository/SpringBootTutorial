package com.zzx.service.impl;


import com.zzx.entity.User;
import com.zzx.mapper.UserMapper;
import com.zzx.service.UserService;
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
