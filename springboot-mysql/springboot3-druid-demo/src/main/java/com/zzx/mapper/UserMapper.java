package com.zzx.mapper;


import org.apache.ibatis.annotations.Mapper;
import com.zzx.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> queryAllUserInfo();
}
