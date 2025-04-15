package com.tb.sensitiveword.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.tb.sensitiveword.constant.GlobalConstants;
import com.tb.sensitiveword.service.ISensitiveWordFilterService;
import com.tb.sensitiveword.util.RedisCache;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 敏感词过滤 Service实现类
 *
 * @author tb
 * @since 2024-04-09
 */
@Service
public class SensitiveWordFilterServiceImpl implements ISensitiveWordFilterService {
    @Resource
    private RedisCache redisCache;

    /**
     * 初始化敏感词到redis
     *
     * @param words
     * @return
     */
    @Override
    public boolean initSensitiveWord2Redis(List<String> words) {
        // 这里的words可以从数据库读取
        if (CollectionUtil.isEmpty(words)) {
            return false;
        }
        //先删除redis中已经存在的旧敏感词数据
        redisCache.deleteObject(GlobalConstants.REDIS_KEY_PREFIX + GlobalConstants.REDIS_KEY);
        // 再插入最新的敏感词数据
        redisCache.setCacheList(GlobalConstants.REDIS_KEY_PREFIX + GlobalConstants.REDIS_KEY, words);
        return true;
    }


    /**
     * 获取redis中的敏感词列表
     *
     * @return
     */
    @Override
    public List<String> sensitiveWordsFromRedis() {
        return redisCache.getCacheList(GlobalConstants.REDIS_KEY_PREFIX + GlobalConstants.REDIS_KEY);
    }
}
