package com.zzx.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_info")
public class User {
    private Integer id;        // 对应数据库中的 `u_id`
    private String userName;   // 对应数据库中的 `u_username`
    private String passWord;   // 对应数据库中的 `u_password`
    private Integer age;       // 对应数据库中的 `u_age`
    private String gender;     // 对应数据库中的 `u_gender`
}
