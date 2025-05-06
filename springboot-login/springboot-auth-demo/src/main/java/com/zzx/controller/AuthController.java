package com.zzx.controller;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.zzx.model.GitHubOAuthInfo;
import com.zzx.service.OauthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/oauth")
public class AuthController {

    @Autowired
    private GitHubOAuthInfo gitHubOAuthInfo;

    @Autowired
    private OauthService oauthService;

    /**
     * Github认证令牌服务器地址
     */
    private static final String ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";

    /**
     * Github认证服务器地址
    */
    private static final String AUTHORIZE_URL = "https://github.com/login/oauth/authorize";

    /**
     * Github资源服务器地址
     */
    private static final String RESOURCE_URL = "https://api.github.com/user";



    /**
     * 前端获取认证的URL，由后端拼接好返回前端进行请求
    */
    @GetMapping("/githubLogin")
    public void githubLogin(HttpServletResponse response) throws IOException {

        // 生成并保存state，忽略该参数有可能导致CSRF攻击
        String state = oauthService.genState();
        // 传递参数response_type、client_id、state、redirect_uri
        String param = "response_type=code&" + "client_id=" + gitHubOAuthInfo.getClientId() + "&state=" + state
                + "&redirect_uri=" + gitHubOAuthInfo.getDirectUrl();

        // 1、请求Github认证服务器
        response.sendRedirect(AUTHORIZE_URL + "?" + param);
    }

    /**
     * GitHub回调方法
     *  code 授权码
     * state 应与发送时一致，防止CSRF攻击
     */
    @GetMapping("/githubCallback")
    public String githubCallback(String code, String state, HttpServletResponse response) throws Exception {
        // 验证state，如果不一致，可能被CSRF攻击
        if(!oauthService.checkState(state)) {
            throw new Exception("State验证失败");
        }

        // 设置JSONObject请求体
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("client_id",gitHubOAuthInfo.getClientId());
        jsonObject.put("client_secret",gitHubOAuthInfo.getClientSecret());
        jsonObject.put("code",code);

        String accessTokenRequestJson = null;
        try{
            long start = System.currentTimeMillis();
            // 请求accessToken，成功获取到后进行下一步信息获取,这里第一次可能会超时
            accessTokenRequestJson = HttpRequest.post(ACCESS_TOKEN_URL)
                    .header("Accept"," application/json")
                    .body(jsonObject.toJSONString())
                    .timeout(30000)
                    .execute().body();
            log.info("请求令牌耗时：{}",System.currentTimeMillis()-start);
        }catch (Exception e){
            log.error("请求令牌API访问异常，异常原因：",e);
            throw new Exception(e);
        }

        log.info("获取到的accessToken为：{}",accessTokenRequestJson);

        JSONObject accessTokenObject = JSONObject.parseObject(accessTokenRequestJson);
        // 如果返回的数据包含error，表示失败，错误原因存储在error_description
        if(accessTokenObject.containsKey("error")) {
            log.error("错误，原因：{}",accessTokenRequestJson);
            throw  new Exception("error_description，令牌获取错误");
        }
        // 如果返回结果中包含access_token，表示成功
        if(!accessTokenObject.containsKey("access_token")) {
            throw  new Exception("获取token失败");
        }

        // 得到token和token_type
        String accessToken = (String) accessTokenObject.get("access_token");
        String tokenType = (String) accessTokenObject.get("token_type");
        String userInfo = null;
        try{
            long start = System.currentTimeMillis();
            // 请求资源服务器获取个人信息
            userInfo = HttpRequest.get(RESOURCE_URL)
                    .header("Authorization", tokenType + " " + accessToken)
                    .timeout(5000)
                    .execute().body();
            log.info("请求令牌耗时：{}",System.currentTimeMillis()-start);
        }catch (Exception e){
            log.error("请求令牌API访问异常，异常原因：",e);
            throw new Exception(e);
        }

        JSONObject userInfoJson = JSONObject.parseObject(userInfo);
        return userInfoJson.toJSONString();
    }

}
