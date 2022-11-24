package com.owinfo.audit.log.service;

import com.owinfo.audit.log.service.entity.Operator;

public class OperatorServiceImpl implements OperatorService {

    @Override
    public Operator getUser() {
        Operator operator = new Operator();
        operator.setUserId("-1");
        operator.setUserName("未知用户");
        return operator;
    }
}
