package com.yudianbank.redis.move.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping
public class MoveController {

    @RequestMapping("isHealthy")
    @ResponseBody
    public Object isHealthy(){
        return "project is running healthy";
    }

}
