package com.zzx.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@ApiModel(description = "用户认证信息")
//@Table(name = "user_auth")
public class UserAuth {

    /**
     * 认证ID
     */
    @ApiModelProperty(value = "认证ID")
    private Long id;

    /**
     * 用户信息ID
     */
    private Long userInfoId;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * 用户密码（加密存储）
     */
    @ApiModelProperty(value = "用户密码")
    private String password;

    /**
     * 用户手机号
     */
    @ApiModelProperty(value = "用户手机号")
    private String phone;

    /**
     * 登录类型 (1手机号, 2QQ, 3Gitee, 4Github)
     */
    @ApiModelProperty(value = "登录类型 (1手机号, 2QQ, 3Gitee, 4Github)")
    private Integer loginType;



    /**
     * 登录时间
     */
    @ApiModelProperty(value = "登录时间")
    private LocalDateTime loginTime;

    /**
     * 登录IP
     */
    @ApiModelProperty(value = "登录IP")
    private String ipAddress;

    /**
     * 登录地址
     */
    @ApiModelProperty(value = "登录地址")
    private String ipSource;

    /**
     * 第三方登录唯一标识（如QQ OpenID、Github ID等）
     */
    @ApiModelProperty(value = "第三方登录唯一标识")
    private String thirdPartyId;

    /**
     * 第三方登录提供商（如QQ、Gitee、Github）
     */
    @ApiModelProperty(value = "第三方登录提供商")
    private String thirdPartyProvider;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    @ApiModelProperty(value = "最后更新时间")
    private LocalDateTime updateTime;

}
