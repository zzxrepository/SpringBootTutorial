package com.zzx.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCodeEnum {

    SUCCESS(20000, "操作成功"),

    FAIL(51000, "操作失败"),;

    private final Integer code;

    private final String desc;

}
