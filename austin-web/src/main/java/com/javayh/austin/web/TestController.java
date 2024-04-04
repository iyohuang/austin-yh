package com.javayh.austin.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {
    @RequestMapping("/test")
    private String test(){
        log.info("----------------------日志启动成功------------------");
        log.error("----------------------错误测试------------------");
        return "hello,yh";
    }
}
