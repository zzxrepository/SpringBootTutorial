package com.zzx.user.controller;

import com.zzx.user.converter.UserConverter;
import com.zzx.user.model.vo.dto.UserInfoDTO;
import com.zzx.user.reponse.ResVo;
import com.zzx.user.repository.entity.User;
import com.zzx.user.service.UserService;
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
    public ResVo<List<UserInfoDTO>> queryAllUserInfo() {
        List<User> userList = userService.queryAllUserInfo();
        List<UserInfoDTO> userInfoDTOList = UserConverter.toUserInfoDTOList(userList);
        return ResVo.success(userInfoDTOList);
    }
}

