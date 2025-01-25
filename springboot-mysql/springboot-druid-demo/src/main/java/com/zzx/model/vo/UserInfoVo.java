package com.zzx.model.vo;

import lombok.Data;

@Data
public class UserInfoVo {
    //返回给前端展示的数据，密码不能展示，性别转化成数字
    private Integer id;
    private String username;
    private Integer age;
    private Integer gender;
}
