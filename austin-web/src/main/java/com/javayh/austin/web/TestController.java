package com.javayh.austin.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @RequestMapping("/test")
    private String test(){
        log.info("----------------------日志启动成功------------------");
        log.error("----------------------错误测试------------------");
        return "hello,yh";
    }
    
    @RequestMapping("/redis")
    private String testRedis(){
        redisTemplate.opsForValue().set("yh","austin");
        return redisTemplate.opsForValue().get("yh");
    }
}
