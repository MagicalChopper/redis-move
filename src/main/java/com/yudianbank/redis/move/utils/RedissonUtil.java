package com.yudianbank.redis.move.utils;

import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;

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
    public Object strRead(RedissonClient redissonClient,String key){
        return redissonClient.getBucket( key ).get();
    }

    /**
     * list读
     * @param redissonClient
     * @param key
     * @return
     */
    public List listRead(RedissonClient redissonClient, String key){
        return redissonClient.getList(key).readAll();
    }

    /**
     * set读
     * @param redissonClient
     * @param key
     * @return
     */
    public Set setRead(RedissonClient redissonClient, String key){
        return redissonClient.getSet(key).readAll();
    }

    /**
     * zset读
     * @param redissonClient
     * @param key
     * @return
     */
    public Collection<ScoredEntry<Object>> zsetRead(RedissonClient redissonClient, String key){
        return  redissonClient.getScoredSortedSet(key).entryRange(0,-1);
    }

    /**
     * hash读
     * @param redissonClient
     * @param key
     * @return
     */
    public Map hashRead(RedissonClient redissonClient, String key){
        return (Map) redissonClient.getMap(key).get(key);
    }
}
