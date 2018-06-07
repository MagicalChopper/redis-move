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

    /**
     * 暂时是获取所有的key过滤掉dubbo开头的，测试完成读写功能
     */
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
            if(obj!=null){
                list.add(obj);
            }
        }
    }

    public Object readUnit(RKeys rKeys,String key){
        Object obj = null;
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
        }
        return obj;
    }
}
