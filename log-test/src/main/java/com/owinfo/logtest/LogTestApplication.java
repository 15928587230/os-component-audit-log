package com.owinfo.logtest;

import com.owinfo.audit.log.config.EnableLogRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author pengtao
 */
@EnableAsync
@SpringBootApplication
public class LogTestApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(LogTestApplication.class, args);

    }

}
