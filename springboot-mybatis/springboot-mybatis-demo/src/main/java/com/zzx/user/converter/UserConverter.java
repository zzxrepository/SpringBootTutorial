package com.zzx.user.converter;

import com.zzx.user.model.enums.UserSexEnum;
import com.zzx.user.model.vo.dto.UserInfoDTO;
import com.zzx.user.repository.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserConverter {

    // 单个转换
    public static UserInfoDTO toUserInfoDTO(User user) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setId(user.getId());
        userInfoDTO.setUsername(user.getUserName());
        userInfoDTO.setAge(user.getAge());
        userInfoDTO.setGender(UserSexEnum.getCodeByString(user.getGender()));
        return userInfoDTO;
    }

    // 批量转换
    public static List<UserInfoDTO> toUserInfoDTOList(List<User> users) {
        // 使用 Java 8 的 stream API 进行批量转换
        return users.stream()
                .map(UserConverter::toUserInfoDTO)  // 对每个 User 对象进行转换
                .collect(Collectors.toList());     // 收集成 List<UserInfoDTO>
    }
}
