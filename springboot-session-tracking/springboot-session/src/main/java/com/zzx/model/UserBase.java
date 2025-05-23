package com.zzx.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;

@Data
@TableName("user_base")
public class UserBase {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String userName;
    private String userPassword;
    private String userMobile;
    private String state;
    private String more;
}