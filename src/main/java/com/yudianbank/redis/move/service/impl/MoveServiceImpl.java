package com.yudianbank.redis.move.service.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yudianbank.redis.move.service.MoveService;
import com.yudianbank.redis.move.utils.RedissonUtil;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RType;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@Service
public class MoveServiceImpl implements MoveService {

    private static final Logger logger = LoggerFactory.getLogger(MoveServiceImpl.class);

    private static final RedissonUtil redissonUtil = new RedissonUtil();

    @Autowired
    RedissonClient redissonClient;


    @Override
    public RKeys getRKeys() {
        return redissonClient.getKeys();
    }

    @Override
    public Object readRedis(String key) {
        return redissonClient.getMapCache(key);
    }

    @Override
    public void writeRedis(Map<String,Object> map) {
        Config config = redissonClient.getConfig();
        config.useSingleServer().setDatabase(15);
        RedissonClient casualRedissonClient = Redisson.create(config);
        Map.Entry<String,Object> entry = (Map.Entry<String, Object>) map.entrySet();
        RBucket<Object> bucket = casualRedissonClient.getBucket(entry.getKey());
        bucket.set(entry.getValue());
    }

    @Override
    public void move() {
        List list = new ArrayList<>();
        RKeys rKeys = redissonClient.getKeys();
        Iterable<String> iterable = rKeys.getKeysByPattern("*");
        Iterator it = iterable.iterator();
        while (it.hasNext()){
            String key = (String) it.next();
            if(key.startsWith("/dubbo/")){
               continue;
            }
            Object obj = readUnit(rKeys,key);
            System.out.println(obj);
            if(obj!=null){
                list.add(obj);
            }
        }
    }

    public Object readUnit(RKeys rKeys,String key){
        Object obj = new Object();
        RType type = rKeys.getType(key);
        try{
            switch (type) {
                case OBJECT:
                    logger.info( "正在读取string，key:{}", key );
                    obj = redissonUtil.strRead( redissonClient, key );
                    break;
                case LIST:
                    logger.info( "正在读取list，key:{}", key );
                    obj = redissonUtil.listRead( redissonClient, key );
                    break;
                case SET:
                    logger.info( "正在读取set，key:{}", key );
                    obj = redissonUtil.setRead( redissonClient, key );
                    break;
                case ZSET:
                    logger.info( "正在读取zset，key:{}", key );
                    obj = redissonUtil.zsetRead( redissonClient, key );
                    break;
                case MAP:
                    logger.info( "正在读取hash，key:{}", key );
                    obj = redissonUtil.hashRead( redissonClient, key );
                    break;
                default:
                    logger.error( "============数据类型异常=============key:{}", key );
            }
        }catch (RuntimeException e){
        logger.info("读取出现异常,key:{},异常信息:{}",key,e.getMessage());
        obj = null;
        }
        return obj;
    }
}
