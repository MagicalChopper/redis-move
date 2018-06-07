package com.yudianbank.redis.move.utils;

import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.client.protocol.ScoredEntry;
import org.redisson.config.Config;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedissonUtil {

    /**
     * String读
     * @param redissonClient
     * @param key
     * @return
     */
    public Object objRead(RedissonClient redissonClient, String key){
        return redissonClient.getBucket(key).get();
    }

    /**
     * list读
     * @param redissonClient
     * @param key
     * @return
     */
    public RList listRead(RedissonClient redissonClient, String key){
        return redissonClient.getList(key);
    }

    /**
     * set读
     * @param redissonClient
     * @param key
     * @return
     */
    public RSet<Object> setRead(RedissonClient redissonClient, String key){
        return redissonClient.getSet(key);
    }

    /**
     * zset读
     * @param redissonClient
     * @param key
     * @return
     */
    public RSortedSet zsetRead(RedissonClient redissonClient, String key){
        return  redissonClient.getSortedSet(key);
    }

    /**
     * hash读
     * @param redissonClient
     * @param key
     * @return
     */
    public RMapCache<Object, Object> hashRead(RedissonClient redissonClient, String key){
        return  redissonClient.getMapCache(key);
    }

    /**
     * object写
     * @param redissonClient
     * @param key
     * @param object
     */
    public void objWrite(RedissonClient redissonClient,String key,Object object){
        redissonClient.getBucket(key).set(object);
    }

    /**
     * list写
     * @param redissonClient
     * @param key
     * @param rList
     */
    public void listWrite(RedissonClient redissonClient,String key,RList rList){
        redissonClient.getList(key).addAll(rList);
    }

    /**
     * set写
     * @param redissonClient
     * @param key
     * @param rSet
     */
    public void setWrite(RedissonClient redissonClient,String key,RSet rSet){
        redissonClient.getSet(key).addAll(rSet);
    }

    /**
     * zset写
     * @param redissonClient
     * @param key
     * @param rSortedSet
     */
    public void zsetWrite(RedissonClient redissonClient,String key,RSortedSet rSortedSet){
        redissonClient.getSortedSet(key).addAll(rSortedSet);
    }

    /**
     * hash写
     * @param redissonClient
     * @param key
     * @param rMap
     */
    public void hashWrite(RedissonClient redissonClient,String key,RMap rMap){
        redissonClient.getMap(key).putAll(rMap);
    }

    /**
     * 换数据库
     * @param redissonClient
     * @param dbNum
     * @return
     */
    public static RedissonClient changeDB(RedissonClient redissonClient,int dbNum){
        Config config = redissonClient.getConfig();
        config.useSingleServer().setDatabase(dbNum);
        return Redisson.create(config);
    }
}
