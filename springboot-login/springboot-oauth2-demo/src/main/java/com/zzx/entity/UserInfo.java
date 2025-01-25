package com.zzx.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@ApiModel(description = "用户个人信息")
public class UserInfo {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;


    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    private String avatar;

    /**
     * 个人网站
     */
    @ApiModelProperty(value = "个人网站")
    private String webSite;

    /**
     * 个人简介
     */
    @ApiModelProperty(value = "个人简介")
    private String intro;

    /**
     * 是否禁用 (0否, 1是)
     */
    @ApiModelProperty(value = "是否禁用 (0否, 1是)")
    private Integer isDisable;
}
