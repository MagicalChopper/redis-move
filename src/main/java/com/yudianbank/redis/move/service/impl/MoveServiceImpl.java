package com.yudianbank.redis.move.service.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yudianbank.redis.move.model.DataResult;
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
import sun.net.www.protocol.http.HttpURLConnection;

import javax.xml.crypto.Data;
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
        List list = read();
    }

    public List<DataResult> read(){
        List list = new ArrayList<>();
        RKeys rKeys = redissonClient.getKeys();//获取Rkeys可以迭代遍历key
        Iterable<String> iterable = rKeys.getKeysByPattern("*");
        Iterator it = iterable.iterator();
        while (it.hasNext()){
            String key = (String) it.next();
            if(key.startsWith("/dubbo/")){
                continue;
            }
            DataResult dataResult = readUnit(rKeys,key);
            if(dataResult.getObj()!=null){
                list.add(dataResult);
            }else{
                logger.info( "==================================================");
            }
        }
        return list;
    }

    /**
     * 单元读
     * @param rKeys
     * @param key
     * @return
     */
    public DataResult readUnit(RKeys rKeys, String key){
        DataResult dataResult = new DataResult();
        dataResult.setKey(key);
        RType type = rKeys.getType(key);
        if(type==null){
            logger.error( "============未获取到数据类型=============key:{}", key );
            return dataResult;
        }
        Object obj = null;

        switch (type) {
            case OBJECT:
                logger.info( "正在读取object，key:{}", key );
                try{
                    obj = redissonUtil.strRead( redissonClient, key );
                    dataResult.setObj(obj);
                }catch (RuntimeException e){
                    logger.error("jackson解析，开头不允许为0，异常信息:{},堆栈信息:{}.type:object,key:{}",e.getMessage(),e.getStackTrace(),key);
                }
                break;
            case LIST:
                logger.info( "正在读取list，key:{}", key );
                try{
                    obj = redissonUtil.listRead( redissonClient, key );
                    dataResult.setObj(obj);
                }catch (RuntimeException e){
                    logger.error("jackson对象转化异常,异常信息：{},堆栈信息：{},type:list,key:{}",e.getMessage(),e.getStackTrace(),key);
                }
                break;
            case SET:
                logger.info( "正在读取set，key:{}", key );
                try{
                    obj = redissonUtil.setRead( redissonClient, key );
                    dataResult.setObj(obj);
                }catch (RuntimeException e){
                    logger.error("jackson解析异常，异常信息：{},堆栈信息：{},type:set,key:{}",e.getMessage(),e.getStackTrace(),key);
                }
                break;
            case ZSET:
                logger.info( "正在读取zset，key:{}", key );
                try{
                    obj = redissonUtil.zsetRead( redissonClient, key );
                    dataResult.setObj(obj);
                }catch (RuntimeException e){
                    logger.error("jackson解析异常，异常信息：{},堆栈信息：{},type:zset,key:{}",e.getMessage(),e.getStackTrace(),key);
                }
                break;
            case MAP:
                logger.info( "正在读取hash，key:{}", key );
                try{
                    obj = redissonUtil.hashRead( redissonClient, key );
                    dataResult.setObj(obj);
                }catch (RuntimeException e){
                    logger.error("jackson解析异常，异常信息：{},堆栈信息：{},type:hash,key:{}",e.getMessage(),e.getStackTrace(),key);
                }
                break;
            default:

        }

        return new DataResult(key,obj);
    }
}
