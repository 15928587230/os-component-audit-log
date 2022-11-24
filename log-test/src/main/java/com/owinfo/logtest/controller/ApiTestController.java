package com.owinfo.logtest.controller;

import com.owinfo.audit.log.config.LogRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author pengtao
 * @email plexpt@gmail.com
 * @date 2021-03-16 16:20
 */
@Slf4j
//@LogRecord(moduleName = "xxxxx", source = "类上的", spare1 = "apikey")
@RestController
@LogRecord(moduleName = "测试模块")
public class ApiTestController {


    @LogRecord("访问了测试接口{{#id}}")
    @RequestMapping("/api/test")
    public String test(@RequestParam("id") String id) {
        return "结果";
    }
}
