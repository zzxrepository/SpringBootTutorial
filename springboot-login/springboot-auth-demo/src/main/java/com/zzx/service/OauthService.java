package com.zzx.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class OauthService {

    private Set<String> stateSet = new HashSet<>();

    /**
     * 生成随机state字符串，这里可以存入Redis或者Set，返回时进行校验，不过要注意失效时间
    */
    public String genState(){
        String state = UUID.randomUUID().toString();
        stateSet.add(state);
        return state;
    }

    /**
     * 校验state，防止CSRF
     * 校验成功后删除
    */
    public boolean checkState(String state){
        if(stateSet.contains(state)){
            stateSet.remove(state);
            return true;
        }
        return false;
    }


}

