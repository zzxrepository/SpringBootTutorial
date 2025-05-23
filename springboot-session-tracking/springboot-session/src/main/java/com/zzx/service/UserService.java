package com.zzx.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzx.mapper.UserBaseMapper;
import com.zzx.model.UserBase;
import com.zzx.util.JwtUtil;
import com.zzx.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired private UserBaseMapper ubMapper;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private RedisUtil redisUtil;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String login(String username, String rawPwd) {
        UserBase u = ubMapper.selectOne(
            new QueryWrapper<UserBase>().eq("user_name", username));
        if (u != null && encoder.matches(rawPwd, u.getUserPassword())) {
            String token = jwtUtil.generateToken(u.getId());
            redisUtil.set(token, u.getId().toString());
            return token;
        }
        return null;
    }
}