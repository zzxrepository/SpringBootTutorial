package com.zzx.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @TableName t_user_info
 */
@TableName(value ="t_user_info")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserInfo extends BaseDO {

    private String email;

    private String nickname;

    private String avatar;

    private String intro;

    private String website;

    private Integer isSubscribe;

    private Integer isDisable;

    private static final long serialVersionUID = 1L;
}