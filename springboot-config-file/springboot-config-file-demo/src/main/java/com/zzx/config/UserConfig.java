package com.zzx.config;

import javax.validation.constraints.*;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "user")
@Validated  // 启用对配置类的校验
public class UserConfig {

    @NotNull(message = "用户名不能为空")
    private String username;

    @Size(min = 8, max = 20, message = "密码长度必须在8到20个字符之间")
    private String password;

    @Min(value = 18, message = "年龄不能小于18岁")
    @Max(value = 100, message = "年龄不能大于100岁")
    private int age;

    @DecimalMin(value = "1000.00", inclusive = true, message = "账户余额不能小于1000")
    @DecimalMax(value = "100000.00", inclusive = true, message = "账户余额不能大于100000")
    private double balance;

    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "邀请码只能包含字母和数字")
    private String inviteCode;

    @Past(message = "出生日期必须是过去的日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @AssertTrue(message = "必须同意服务条款")
    private boolean agreeToTerms;

    @AssertFalse(message = "不能勾选订阅邮件")
    private boolean subscribeToNewsletter;

    @Size(min = 1, max = 5, message = "兴趣爱好数量必须在1到5个之间")
    private List<String> hobbies;

    @Email(message = "邮箱格式不正确")
    private String email;  // 添加邮箱字段

    @Override
    public String toString() {
        return "UserConfig {\n" +
                "  username='" + username + "'\n" +
                "  password='" + password + "'\n" +
                "  age=" + age + "\n" +
                "  balance=" + balance + "\n" +
                "  inviteCode='" + inviteCode + "'\n" +
                "  birthDate=" + birthDate + "\n" +
                "  agreeToTerms=" + agreeToTerms + "\n" +
                "  subscribeToNewsletter=" + subscribeToNewsletter + "\n" +
                "  hobbies=" + hobbies + "\n" +
                "  email='" + email + "'\n" +
                "}";
    }
}
