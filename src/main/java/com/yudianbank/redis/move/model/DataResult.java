package com.yudianbank.redis.move.model;

public class DataResult {

    private String key;
    private Object obj;

    public DataResult() {
    }

    public DataResult(String key, Object obj) {
        this.key = key;
        this.obj = obj;
    }

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
}
