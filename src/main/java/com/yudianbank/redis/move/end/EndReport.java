package com.yudianbank.redis.move.end;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndReport {

    Logger logger = LoggerFactory.getLogger(EndReport.class);

    public void init(){
        logger.info("====================END=======================");
    }

    public void destory(){
        logger.info("project destoryed");
    }

}
