package com.zzx.controller;

import com.zzx.converter.UserConverter;
import com.zzx.model.vo.UserInfoVo;
import com.zzx.reponse.ResVo;
import com.zzx.entity.User;
import com.zzx.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {
    @Resource
    private UserService userService;

    @GetMapping("queryAllUserInfo")  // 使用 GET 请求
    public ResVo<List<UserInfoVo>> queryAllUserInfo() {
        List<User> userInfoList = userService.queryAllUserInfo();
        return ResVo.success(UserConverter.toUserInfoDTOList(userInfoList));
    }
}

