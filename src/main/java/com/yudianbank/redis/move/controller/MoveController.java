package com.yudianbank.redis.move.controller;

import com.yudianbank.redis.move.service.MoveService;
import org.redisson.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        logger.info("=================end=================");
        return "succ";
    }

    @RequestMapping("test")
    @ResponseBody
    public String test(){
        RBucket rBucket = redissonClient.getBucket("proY0175170724");
        Object bucket = rBucket.get();
        Class bucketClass = bucket.getClass();

        RList rList = redissonClient.getList("testBatch");
        Object list = rList.readAll();
        Class listClass = list.getClass();

//        RSet rSet = redissonClient.getSet("escape_check_url_authority");
//        Set set = rSet.readAll();
//        Class setClass = set.getClass();
//
//        RSortedSet rSortedSet = redissonClient.getSortedSet("redisson__idle__set__{ADJUST#22#200655#net.engining.pcx.cc.param.model.PostCode}");
//        Set sortSet = rSortedSet.readAll();
//        Class sortSetClass = sortSet.getClass();

        RMap rMap = redissonClient.getMap("authority_cache_appManage");
        Set<Map.Entry> entrySet = rMap.readAllEntrySet();
        return "succ";
    }

}
