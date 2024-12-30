package com.zzx.user.repository.mapper;


import com.zzx.user.repository.entity.User;

import java.util.List;

public interface UserMapper {
    List<User> queryAllUserInfo();
}
