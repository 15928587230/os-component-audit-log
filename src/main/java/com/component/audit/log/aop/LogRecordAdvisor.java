package com.component.audit.log.aop;

import com.component.audit.log.config.LogRecord;
import org.aopalliance.aop.Advice;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;

import java.lang.reflect.Method;

public class LogRecordAdvisor extends StaticMethodMatcherPointcutAdvisor {

    // 执行拦截之后的逻辑
    public LogRecordAdvisor(Advice advice) {
        setAdvice(advice);
    }

    // 匹配需要拦截的方法
    @Override
    public boolean matches(Method method, Class<?> clazz) {
        LogRecord annotation = findAnnotation(method, LogRecord.class);
        return annotation != null;
    }

    private LogRecord findAnnotation(Method method, Class<LogRecord> logRecordClass) {
        LogRecord annotation = method.getAnnotation(logRecordClass);
        return annotation ;
    }
}
