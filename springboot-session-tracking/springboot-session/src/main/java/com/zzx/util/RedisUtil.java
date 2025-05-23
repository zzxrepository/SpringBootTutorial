package com.zzx.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类，支持常用数据结构操作
 */
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 指定 key 的过期时间
     */
    public boolean expire(String key, long time) {
        return redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * 获取 key 的剩余过期时间（秒）
     */
    public long getTime(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断 key 是否存在
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 移除 key 的过期时间
     */
    public boolean persist(String key) {
        return redisTemplate.boundValueOps(key).persist();
    }

    // ------------------- String 类型操作 -------------------

    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, String value, long time) {
        if (time > 0) {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    public void batchSet(Map<String, String> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    public void batchSetIfAbsent(Map<String, String> map) {
        redisTemplate.opsForValue().multiSetIfAbsent(map);
    }

    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public Double increment(String key, double delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    // ------------------- Set 类型操作 -------------------

    public void sSet(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public Set<Object> members(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public Set<Object> randomMembers(String key, long count) {
        return (Set<Object>) redisTemplate.opsForSet().randomMembers(key, count);
    }

    public Object randomMember(String key) {
        return redisTemplate.opsForSet().randomMember(key);
    }

    public Object pop(String key) {
        return redisTemplate.opsForSet().pop(key);
    }

    public long size(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    public boolean sHasKey(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    public boolean move(String key, String value, String destKey) {
        return redisTemplate.opsForSet().move(key, value, destKey);
    }

    public void remove(String key, Object... values) {
        redisTemplate.opsForSet().remove(key, values);
    }

    // ------------------- Hash 类型操作 -------------------

    public void add(String key, Map<String, String> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    public Map<Object, Object> getHashEntries(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public boolean hashKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    public Object getMapValue(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    public Long hashDelete(String key, String... hashKeys) {
        return redisTemplate.opsForHash().delete(key, (Object[]) hashKeys);
    }

    public Long hashIncrement(String key, String hashKey, long delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    public Double hashIncrement(String key, String hashKey, Double delta) {
        return redisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    public Set<Object> hashKeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    public long hashSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    // ------------------- List 类型操作 -------------------

    public void leftPush(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    public List<Object> range(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    public void leftPushAll(String key, String... values) {
        redisTemplate.opsForList().leftPushAll(key, values);
    }

    public void rightPushAll(String key, String... values) {
        redisTemplate.opsForList().rightPushAll(key, values);
    }

    public long listLength(String key) {
        return redisTemplate.opsForList().size(key);
    }

    public Object leftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    public Object rightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }
}