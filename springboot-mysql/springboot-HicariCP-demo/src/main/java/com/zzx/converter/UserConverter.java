package com.zzx.converter;

import com.zzx.entity.User;
import com.zzx.enums.UserSexEnum;
import com.zzx.model.vo.UserInfoVo;

import java.util.List;
import java.util.stream.Collectors;

public class UserConverter{
    // 单个转换
    public static UserInfoVo toUserInfoDTO(User user) {
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setId(user.getId());
        userInfoVo.setUsername(user.getUserName());
        userInfoVo.setAge(user.getAge());
        userInfoVo.setGender(UserSexEnum.getCodeByString(user.getGender()));
        return userInfoVo;
    }

    // 批量转换
    public static List<UserInfoVo> toUserInfoDTOList(List<User> users) {
        // 使用 Java 8 的 stream API 进行批量转换
        return users.stream()
                .map(UserConverter::toUserInfoDTO)  // 对每个 User 对象进行转换
                .collect(Collectors.toList());     // 收集成 List<UserInfoDTO>
    }
}
