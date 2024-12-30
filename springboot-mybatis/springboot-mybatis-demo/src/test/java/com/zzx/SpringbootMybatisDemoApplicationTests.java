package com.zzx;

import com.zzx.user.repository.entity.User;
import com.zzx.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@MapperScan("com.zzx.user.repository.mapper")  // 扫描 Mapper 接口所在的包
class SpringbootMybatisDemoApplicationTests {

    @Resource
    private UserService userService;

    @Test
    public void getAllUserInfo(){
        List<User> users = userService.queryAllUserInfo();
        users.forEach(System.out::println);
    }

}
