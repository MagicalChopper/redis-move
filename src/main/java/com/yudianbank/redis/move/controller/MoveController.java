package com.yudianbank.redis.move.controller;

import com.yudianbank.redis.move.service.MoveService;
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

    @RequestMapping("move")
    @ResponseBody
    public Object move(){
        moveService.move();
        logger.info("=================end=================");
        return "succ";
    }

}
