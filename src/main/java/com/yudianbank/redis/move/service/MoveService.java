package com.yudianbank.redis.move.service;

import org.redisson.api.RKeys;

import java.util.Map;

public interface MoveService {
    /**
     * 获取所有key
     * @return
     */
    RKeys getRKeys();

    /**
     * 根据key读
     * @return
     */
    Object readRedis(String key);

    /**
     * 写
     */
    void writeRedis(Map<String,Object> map);

    void move();
}
