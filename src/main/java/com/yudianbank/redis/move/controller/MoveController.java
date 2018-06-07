package com.yudianbank.redis.move.controller;

import com.yudianbank.redis.move.service.MoveService;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping
public class MoveController {

    private static final Logger logger = LoggerFactory.getLogger( MoveController.class);

    @Autowired
    MoveService moveService;

    @Autowired
    RedissonClient redissonClient;

    @RequestMapping("move")
    @ResponseBody
    public Object move(){
        moveService.move();
        return "succ";
    }

    @RequestMapping("test")
    @ResponseBody
    public String test(){
//        RedissonClient redissonClient0 = changeDB(redissonClient,0);
//        redissonClient0.getBucket("lhtest").set("test");
//
//        RedissonClient redissonClient15 = changeDB(redissonClient,15);
//        redissonClient15.getBucket("lhtest").set("test");

        /**
         * obj
         */
//        Object object = redissonClient0.getBucket("proY0175170724").get();

        /**
         * list
         */
//        RList rList = redissonClient0.getList("testBatch");

        /**
         * set
         */
//        RSet rSet = redissonClient0.getSet("escape_check_url_authority");

        /**
         * zset
         */
//        RSortedSet rSortedSet = redissonClient.getSortedSet("redisson__idle__set__{ADJUST#22#200655#net.engining.pcx.cc.param.model.PostCode}");

        /**
         * hash
         */
//        RMap rMap = redissonClient.getMap("authority_cache_appManage");


//        RedissonClient redissonClient15 = changeDB(redissonClient,15);
//
//        redissonClient15.getBucket("proY0175170724").set(object);
//
//        redissonClient15.getList("testBatch").addAll(rList);

//        redissonClient15.getSet("escape_check_url_authority").addAll("escape_check_url_authority");//异常

//        redissonClient.getSortedSet("redisson__idle__set__{ADJUST#22#200655#net.engining.pcx.cc.param.model.PostCode}").addAll(rSortedSet);//异常

//        redissonClient.getMap("authority_cache_appManage").putAll(rMap);

        return "succ";
    }

    public static RedissonClient changeDB(RedissonClient redissonClient,int dbNum){
        Config config = redissonClient.getConfig();
        config.useSingleServer().setDatabase(dbNum);
        return Redisson.create(config);
    }

}
