package com.zzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzx.entity.UserInfo;
import com.zzx.service.UserInfoService;
import com.zzx.mapper.UserInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author flyvideo
* @description 针对表【t_user_info】的数据库操作Service实现
* @createDate 2025-01-02 20:48:05
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService{

}




