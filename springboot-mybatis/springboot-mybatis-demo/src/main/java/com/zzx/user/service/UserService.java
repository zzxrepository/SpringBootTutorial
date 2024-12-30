package com.zzx.user.service;

import com.zzx.user.repository.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;


public interface UserService {

    List<User> queryAllUserInfo();
}
