package com.zzx.user.model.vo.dto;

import lombok.Data;

@Data
public class UserInfoDTO {
    // 返回给前端的用户信息，不需要密码，同时要将性别转化成数字
    private Integer id;
    private String username;
    private Integer age;
    private Integer gender;
}
