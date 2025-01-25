package com.zzx.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzx.entity.User;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    List<User> queryAllUserInfo();
}
