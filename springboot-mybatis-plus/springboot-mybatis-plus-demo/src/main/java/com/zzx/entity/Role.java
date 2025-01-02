package com.zzx.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @TableName t_role
 */
@TableName(value ="t_role")
@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseDO {

    private String roleName;

    private Integer isDisable;

    private static final long serialVersionUID = 1L;
}