package com.yudianbank.redis.move.service.impl;

import com.yudianbank.redis.move.model.DataResult;
import com.yudianbank.redis.move.service.MoveService;
import com.yudianbank.redis.move.utils.RedissonUtil;
import org.redisson.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class MoveServiceImpl implements MoveService {

    private static final Logger logger = LoggerFactory.getLogger(MoveServiceImpl.class);

    private static final RedissonUtil redissonUtil = new RedissonUtil();

    /**
     * 从哪个数据库读
     */
    private static final int DB_READ_FROM = 0;

    /**
     * 写到哪个数据库
     */
    private static final int DB_WRITE_TO = 15;

    /**
     * 根据"read"获取读取失败的key的list列表
     */
    private static final String READ_ERR_LIST = "read";

    /**
     * 根据"write"获取写入失败的key的list列表
     */
    private static final String WRITE_ERR_LIST = "write";

    @Autowired
    RedissonClient redissonClient;

    /**
     * 暂时是获取所有的key过滤掉dubbo开头的，测试完成读写功能
     */
    @Override
    public void move() {
        List<DataResult> list = read();
        Map<String,List<String>> map = write(list);
        logger.info("====================END=======================");
    }

    /**
     * 读取所有
     * @return
     */
    public List<DataResult> read(){
        RedissonClient readRedissonClient = redissonUtil.changeDB(redissonClient,DB_READ_FROM);
        List list = new ArrayList<>();
        RKeys rKeys = readRedissonClient.getKeys();//获取Rkeys可以迭代遍历key
        Iterable<String> iterable = rKeys.getKeysByPattern("*");
        Iterator it = iterable.iterator();
        while (it.hasNext()){
            String key = (String) it.next();
            if(key.startsWith("/dubbo/")){
                continue;
            }
            DataResult dataResult = readUnit(readRedissonClient,rKeys,key);
            list.add(dataResult);
        }
        return list;
    }

    /**
     * 写入数据,返回失败的key,一个map结构，分为read失败和write失败
     * 可以分别根据字符串read,write获取对应是读取失败，写入失败的的key的list
     * @param list
     * @return
     */
    public Map<String,List<String>> write(List<DataResult> list){
        RedissonClient writeRedissonClient = redissonUtil.changeDB(redissonClient,DB_WRITE_TO);
        logger.info("写入的数据库是：{}",writeRedissonClient.getConfig().useSingleServer().getDatabase());
        List<String> readErrList = new ArrayList();
        List<String> writeErrList = new ArrayList<>();
        for (DataResult dataResult : list) {
            if(dataResult.getObj()==null){
                readErrList.add(dataResult.getKey());
                continue;
            }
            String writeErrKey = writeUnit(writeRedissonClient,dataResult);
            if(writeErrKey!=null){
                writeErrList.add(writeErrKey);
            }
        }
        Map<String,List<String>> map = new HashMap<>();
        map.put(READ_ERR_LIST,readErrList);
        map.put(WRITE_ERR_LIST,writeErrList);
        return map;
    }

    /**
     * 单元读，一次读取一个键值对
     * @param readRedissonClient
     * @param rKeys
     * @param key
     * @return
     */
    public DataResult readUnit(RedissonClient readRedissonClient,RKeys rKeys, String key){
        DataResult dataResult = new DataResult();
        dataResult.setKey(key);
        RType type = rKeys.getType(key);
        dataResult.setKeyType(type);
        if(type==null){
            logger.error( "============未获取到数据类型=============key:{}", key );
            return dataResult;
        }
        Object obj;
        switch (type) {
            case OBJECT:
                logger.info( "正在读取object，key:{}", key );
                try{
                    obj = redissonUtil.objRead( readRedissonClient, key );
                    dataResult.setObj(obj);
                }catch (RuntimeException e){
                    logger.error("jackson解析，开头不允许为0，异常信息:{},堆栈信息:{}.type:object,key:{}",e.getMessage(),e.getStackTrace(),key);
                }
                break;
            case LIST:
                logger.info( "正在读取list，key:{}", key );
                try{
                    obj = redissonUtil.listRead( readRedissonClient, key );
                    dataResult.setObj(obj);
                }catch (RuntimeException e){
                    logger.error("jackson对象转化异常,异常信息：{},堆栈信息：{},type:list,key:{}",e.getMessage(),e.getStackTrace(),key);
                }
                break;
            case SET:
                logger.info( "正在读取set，key:{}", key );
                try{
                    obj = redissonUtil.setRead( readRedissonClient, key );
                    dataResult.setObj(obj);
                }catch (RuntimeException e){
                    logger.error("jackson解析异常，异常信息：{},堆栈信息：{},type:set,key:{}",e.getMessage(),e.getStackTrace(),key);
                }
                break;
            case ZSET:
                logger.info( "正在读取zset，key:{}", key );
                try{
                    obj = redissonUtil.zsetRead( readRedissonClient, key );
                    dataResult.setObj(obj);
                }catch (RuntimeException e){
                    logger.error("jackson解析异常，异常信息：{},堆栈信息：{},type:zset,key:{}",e.getMessage(),e.getStackTrace(),key);
                }
                break;
            case MAP:
                logger.info( "正在读取hash，key:{}", key );
                try{
                    obj = redissonUtil.hashRead( readRedissonClient, key );
                    dataResult.setObj(obj);
                }catch (RuntimeException e){
                    logger.error("jackson解析异常，异常信息：{},堆栈信息：{},type:hash,key:{}",e.getMessage(),e.getStackTrace(),key);
                }
                break;
        }
        return dataResult;
    }

    /**
     * 单元写入，一次写入一个键值对
     * @param writeRedissonClient
     * @param dataResult
     * @return
     */
    public String writeUnit(RedissonClient writeRedissonClient,DataResult dataResult){
        RType type = dataResult.getKeyType();
        String key = dataResult.getKey();
        String writeErrKey = null;
        Object object = dataResult.getObj();
        switch (type) {
            case OBJECT:
                logger.info( "正在写入object，key:{}", key );
                try{
                    redissonUtil.objWrite( writeRedissonClient,key,object );
                }catch (RuntimeException e){
                    logger.error("object写入失败,key:{},异常信息：{},堆栈信息：{}",e.getMessage(),e.getStackTrace(),key);
                    writeErrKey = key;
                }
                break;
            case LIST:
                logger.info( "正在写入list，key:{}", key );
                try{
                    redissonUtil.listWrite(writeRedissonClient,key, (RList) object );
                }catch (RuntimeException e){
                    logger.error("list写入失败,key:{},异常信息：{},堆栈信息：{}",e.getMessage(),e.getStackTrace(),key);
                    writeErrKey = key;
                }
                break;
            case SET:
                logger.info( "正在写入set，key:{}", key );
                try{
                    redissonUtil.setWrite(writeRedissonClient,key, (RSet) object );
                }catch (RuntimeException e){
                    logger.error("set写入失败,key:{},异常信息：{},堆栈信息：{}",e.getMessage(),e.getStackTrace(),key);
                    writeErrKey = key;
                }
                break;
            case ZSET:
                logger.info( "正在写入zset，key:{}", key );
                try{
                    redissonUtil.zsetWrite(writeRedissonClient,key, (RSortedSet) object );
                }catch (RuntimeException e){
                    logger.error("zset写入失败,key:{},异常信息：{},堆栈信息：{}",e.getMessage(),e.getStackTrace(),key);
                    writeErrKey = key;
                }
                break;
            case MAP:
                logger.info( "正在写入hash，key:{}", key );
                try{
                    redissonUtil.hashWrite(writeRedissonClient,key, (RMap) object );
                }catch (RuntimeException e){
                    logger.error("hash写入失败,key:{},异常信息：{},堆栈信息：{}",e.getMessage(),e.getStackTrace(),key);
                    writeErrKey = key;
                }
                break;
        }
        return writeErrKey;
    }
}
