package com.zzx.user.model.enums;

public enum UserSexEnum {
    M(1, "男"), // M对应男，值为 1
    F(0, "女"); // F对应女，值为 0

    private int code;         // 对应的数字值（1 或 0）
    private String description; // 性别描述（男 或 女）

    // 构造方法，用于设置枚举常量的描述和对应的代码
    UserSexEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    // 获取性别描述
    public String getDescription() {
        return description;
    }

    // 获取对应的数字代码
    public int getCode() {
        return code;
    }

    // 根据传入的字符串 'M' 或 'F' 获取对应的性别枚举
    public static UserSexEnum fromString(String sexStr) {
        for (UserSexEnum sex : UserSexEnum.values()) {
            if (sex.name().equalsIgnoreCase(sexStr)) {
                return sex;
            }
        }
        throw new IllegalArgumentException("无效的性别字符串: " + sexStr);
    }

    // 根据 'M' 或 'F' 获取对应的数字代码
    public static int getCodeByString(String sexStr) {
        UserSexEnum sex = fromString(sexStr);
        return sex.getCode();
    }
}
