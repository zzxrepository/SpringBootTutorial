package com.zzx.user.repository.entity;


import lombok.Data;

@Data
public class User {
    private Integer id;        // 对应数据库中的 `u_id`
    private String userName;   // 对应数据库中的 `u_username`
    private String passWord;   // 对应数据库中的 `u_password`
    private Integer age;       // 对应数据库中的 `u_age`
    private String gender;     // 对应数据库中的 `u_gender`
}
