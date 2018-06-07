package com.yudianbank.redis.move.model;

import org.redisson.api.RType;


/**
 * 返回结果封装
 */
public class DataResult {

    private String key;//数据key
    private Object obj;//数据value
    private RType keyType;//key类型

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public RType getKeyType() {
        return keyType;
    }

    public void setKeyType(RType keyType) {
        this.keyType = keyType;
    }
}
