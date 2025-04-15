# 密码加密——加盐算法（两种方式）



### 

密码安全是一件很重要的事情，所以一定要谨慎对待

常见的主要是3种方式

1. 明文
2. MD5加密
3. 加盐算法

首先明文肯定是不可取的，在数据库中明文存储密码风险实在是太大了

简单来说，使用MD5就是将一串字符串通过某特定的算法来将其变成另一种形式，这样子就在外观上起到了加密的效果，但是由于背后的算法是固定的，所以每一个字符串都有固定的MD5格式

密码破解程序可以是暴力破解：将得到的密码使用MD5转换成哈希，之后将得到的哈希与最初的哈希进行比较，要是匹配就说明已经破解了密码，但是这种方法的时间复杂度很高，会耗时很久

[彩虹表](https://so.csdn.net/so/search?q=彩虹表&spm=1001.2101.3001.7020)：彩虹表记录了几乎所有字符串的MD5对照表

有了彩虹表MD5就相当于是不存在了，因为一种字符串就只有一种特定的MD5格式

### 手写一个加盐算法

首先要理解“盐”的概念，他就是一个随机值，没有任何规律

> 这里约定密码的最终格式都是 盐值(32位)$加密后的密码(32位）

加密的实现思路：

每次调用的时候都会随机生成一个盐值（随机、唯一） + 用户输入的密码（使用MD5） = 加密的密码，盐值(32位) + $ + 加密密码(32位) = 最终的密码格式

解密（验证密码）的实现思路：

解密的时候需要两个密码 ： 用户输入的明文待验证密码 、 存储在数据库中的最终密码（自定义格式： 盐值(32位)$加密后的密码(32位)）

解密（验证密码）的核心在于**得到 盐值**

解密的时候，首先从最终数据库中的密码中来得到盐值，之后将用户输入的明文待验证密码加上这个盐值，生成加密后的密码，然后使用盐值 + 分隔符 + 加密后的密码 生成 最终密码格式，再与数据库中最终的密码格式进行比对

要是一样的，那就说明这个用户输入的密码是没有问题的，要是不对就说明密码输入错误

> 最重要的是先理解加盐 解密的实现思路，这是最核心的！！！

> 就算使用加盐算法来对密码加密，也不能保证就一定是安全的，可以针对一个盐值来生成一个彩虹表，暴力破解也是可以的，但是这只是破解了一个账号密码，所以破解的成本是极大的，当破解的成本远大于收益的时候，可以看做是安全的

解密（验证密码）具体的实现步骤：

1. 从数据库中真正的最终密码中得到盐值
2. 将用户输入的明文密码+盐值 = 加密后的密码（使用MD5）
3. 使用盐值 + 分隔符 + 加密后的密码 生成 最终密码（最终密码的格式）
4. 对比生成的最终密码和数据库中的最终密码是否相等

要是相等就说明用户名和密码都是对的，要是不对，就说明密码输入错误

> 为什么就是能验证成功呢？
>
> 最终比对的就是三个部分： 盐值 我就是从数据库中的最终密码中拿的前32位，肯定是一样的，$都是一样的，加密的部分都是[MD5加密](https://so.csdn.net/so/search?q=MD5加密&spm=1001.2101.3001.7020)的，所以 也一定是一样的，所以能登录成功

具体的代码：

在common包下面建一个PasswordUtils类

```java
package com.example.demo.common;

import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.UUID;

public class PasswordUtils{

    /**
     * 1.加盐并生成最终的密码
     * @param password 明文的密码
     * @return 最终生成的密码
     */
    public static String encrypt(String password){
        //a.产生盐值
        //UUID.randomUUID()会生成32位数字+4位-，是随机的唯一的，将4位-去掉就得到32位数字的盐值
        String salt = UUID.randomUUID().toString().replace("-","");
        //生成加盐后的密码(需要使用MD5)
        String saltPassword = DigestUtils.md5DigestAsHex((salt + password).getBytes());
        //生成最终的密码格式
        String finalPassword = salt + "$" + saltPassword;
        return finalPassword;
    }

    /**
     * 2.加盐并生成最终密码格式（方法一的重载），区别于上面的方法：这个方法是用来解密的，给定了盐值，生成一个最终密码，
     后面要和正确的最终密码进行比对
     * @param password 需要验证的明文密码
     * @param salt
     * @return
     */
    public static  String encrypt(String password, String salt){
        //1.生成一个加密后的密码
        String saltPassword = DigestUtils.md5DigestAsHex((salt + password).getBytes());
        //2.生成最终的密码（待验证）
        String finalPassword = salt + "$" + saltPassword;
        return finalPassword;
    }

    /**
     * 3.验证密码
     * @param inputPassword  登录用户输入的明文密码
     * @param finalPassword  数据库中实际的最终密码格式
     * @return
     */
    public static boolean check(String inputPassword, String finalPassword){
        //首先判断这两个参数到底有没有值,数据库中的最终密码是不是65位
        if(StringUtils.hasLength(inputPassword) && StringUtils.hasLength(finalPassword)
        && finalPassword.length() == 65){
            //a.首先从最终的密码中得到盐值
            //使用$将finalPassword划分成两个部分，前面的32位的部分就是盐值
            //注意：这里的$是被认为是一个通配符，所以要转义一下
            String salt = finalPassword.split("\\$")[0];
            //b.使用之前加密的方法，生成最终的密码格式（待验证）
            String checkPassword = encrypt(inputPassword,salt);
            if(checkPassword.equals(finalPassword)){
                return true;
            }
        }
        return false;
    }
}
```

在写完了加盐算法之后，就要修改一下博客的具体调用了

在userinfoController中的注册接口：

```java
@RequestMapping("/reg")
public AjaxResult reg(UserInfo userinfo) {
    //非空判断
    //虽然前端已经进行了非空检查，但是用户可能会通过别的方式直接访问url绕过前端的非空校验，所以作为后端，应该要考虑到这一点
    //所以在后端也是要写非空校验的
    if (userinfo == null || !StringUtils.hasLength(userinfo.getUsername()) ||
        !StringUtils.hasLength(userinfo.getPassword())){
            return AjaxResult.fail(-1,"非法参数");
    }
    //不是空的话，就直接返回成功的响应就行了
    //这里响应的是1，所以前端在进行成功判断的时候才有result.data == 1这一条

    //注册成功之后要将密码加盐，写进数据库中
    userinfo.setPassword(PasswordUtils.encrypt(userinfo.getPassword()));
    return AjaxResult.success(userService.reg(userinfo));
}
```

在userinfoController中的登录接口：

```java
@RequestMapping("/login")
public AjaxResult login(HttpServletRequest request, String username, String password) {
    //1.进行非空判断
    if (!StringUtils.hasLength(username) || !StringUtils.hasLength(password)){
        //说明没有传入任何参数
        return AjaxResult.fail(-1,"非法请求");
    }
    //2.查询数据库
    UserInfo userinfo = userService.getUserByName(username);
    if (userinfo != null && userinfo.getId() > 0) {
        //能获得id就说明用户名一定是在数据库中，说明是有效用户

        //使用自己写的加盐算法来判断登录
        //password是输入的待验证的明文密码，userinfo.getPassword()得到的是数据库中正确的最终密码格式
        if (PasswordUtils.check(password,userinfo.getPassword())){
            //将用户的session存储下来
            //参数为true：要是没有session就创建一个会话
            HttpSession session = request.getSession(true);
            //设置session的key和value
            session.setAttribute(ApplicationVariable.USER_SESSION_KEY,userinfo);
            //要是密码正确，在将数据返回之前，考虑到隐私，隐藏密码
            userinfo.setPassword("");
            return AjaxResult.success(userinfo);
        }
    }
    return AjaxResult.fail(0,null);
}
```

以上就是自己手写的一个加盐算法

实际上，springboot官方也提供了一种更加齐全的安全框架： spring security

### spring security

要想使用spring security首先要先引入依赖（可以通过插件Edit Starters来引入依赖）

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

在spring security框架中在项目启动的时候，会**自动注入**登录的页面，在一般的项目中都是有自己的登录页面的，所以不需要自动注入登录，所以要将其去掉

在项目的启动类前面的@SpringBootApplication注解加上排除SecurityAutoConfiguration.class这个类对象就行了

```java
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

//关闭spring security的验证
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class Demo3Application {
    public static void main(String[] args) {
        SpringApplication.run(Demo3Application.class, args);
    }
}
```

在单元测试中使用spring security中的密码加盐算法

```java
package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class Demo3ApplicationTests {

    @Test
    void contextLoads() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String str = "111";
        //进行加密
        String finalPassword = bCryptPasswordEncoder.encode(str);
        System.out.println(finalPassword);
        //验证密码
        String inputPassword1 = "123";
        String inputPassword2 = "111";
        //inputPassword是用户输入的密码（待验证），finalPassword是存储在数据库中的最终密码格式
        System.out.println(bCryptPasswordEncoder.matches(inputPassword1,finalPassword));
        System.out.println(bCryptPasswordEncoder.matches(inputPassword2,finalPassword));
    }

}
```

![image-20230412171450323](https://i-blog.csdnimg.cn/blog_migrate/8c4bb8a5bc44e548429de1426b0882f3.png)

加密之后的最终密码格式： $2a10 1010BXpuKmotUdqoS3rFE59anOTrSfk7gCYX5wfsg9ZblBHvc79EyVFOi

spring security中的最终密码的格式：

![image-20230412170137167](https://i-blog.csdnimg.cn/blog_migrate/bf111cea9e852b9ebbc6044e953ae99e.png)

其实spring security的加盐算法就是bCryptPasswordEncoder对象的encode方法和matches方法

加密的时候encode方法传的参数是用户输入的密码

解密（验证）的时候，使用的matches方法的参数分别是用户输入的明文密码 和 数据库中最终密码格式

所以自己手写一个加盐算法之后再去看spring security调用就会很简单，因为思想都是一样的，所以先理解实现思路很重要！









# 参考文献

- <https://blog.csdn.net/m0_60354608/article/details/130112235>