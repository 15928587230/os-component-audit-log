package com.owinfo.logtest.service;

import com.owinfo.audit.log.service.OperatorService;
import com.owinfo.audit.log.service.entity.Operator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author pengtao
 * @email plexpt@gmail.com
 * @date 2021-03-16 16:45
 */
@Configuration
public class OperatorConfiguration  {

    @Bean
    public OperatorService operatorService() {
        return new OperatorService() {
            @Override
            public Operator getUser() {
                Operator operator = new Operator();
                operator.setUserId("11");
                operator.setUserName("系统管理员");
                return operator;
            }
        };
    }
}
