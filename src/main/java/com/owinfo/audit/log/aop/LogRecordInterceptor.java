package com.owinfo.audit.log.aop;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.owinfo.audit.log.aop.parse.LogRecordValueParser;
import com.owinfo.audit.log.autoconfiguration.AuditLogProperties;
import com.owinfo.audit.log.config.AuditLogConstant;
import com.owinfo.audit.log.config.LogRecord;
import com.owinfo.audit.log.config.LogStatus;
import com.owinfo.audit.log.service.LogRecordService;
import com.owinfo.audit.log.service.OperatorService;
import com.owinfo.audit.log.service.entity.LogRecordEntity;
import com.owinfo.audit.log.service.entity.Operator;
import com.owinfo.audit.log.util.IPUtils;
import com.owinfo.audit.log.util.LogRecordContext;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
public class LogRecordInterceptor extends LogRecordValueParser implements MethodInterceptor {
    private OperatorService operatorService;
    private AuditLogProperties properties;
    private LogRecordService logRecordService;

    public LogRecordInterceptor(OperatorService operatorService, AuditLogProperties properties, LogRecordService logRecordService) {
        this.operatorService = operatorService;
        this.properties = properties;
        this.logRecordService = logRecordService;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        return execute(invocation, invocation.getThis(), method, invocation.getArguments());
    }

    private Object execute(MethodInvocation invoker, Object target, Method method, Object[] args) throws Throwable {
        Class<?> targetClass = getTargetClass(target);
        Object ret = null;
        String errorMsg = "";
        Throwable throwable = null;
        try {
            LogRecordContext.clear();
            ret = invoker.proceed();
        } catch (Exception e) {
            errorMsg = e.getMessage();
            throwable = e;
        }

        try {
            // operatorService注入获取用户的方法, 比如 UserContextHolder.getContext().getUser()
            Operator operator = operatorService.getUser();

            //  日志不拦截两种方式:
            //      operator.isRecordOrNot() = false, 默认true
            //      请求头放入下面的header(适用于feign调用)
            HttpServletRequest request = IPUtils.getRequest();
            if(request != null && !AuditLogConstant.FEIGN_HEADER_VALUE.equals(request.getHeader(AuditLogConstant.FEIGN_HEADER_NAME))) {
                if (operator != null && operator.isRecordOrNot()) {
                    LogRecordEntity logRecordEntity = collectLogRecord(method, method.getAnnotation(LogRecord.class));
                    if (throwable != null || !isEmpty(errorMsg)) {
                        logRecordEntity.setLogStatus(LogStatus.FAILURE);
                    }
                    recordExecute(ret, method, args, logRecordEntity, targetClass, errorMsg, operator);
                }
            }
        } catch (Exception t) {
            //记录日志错误不要影响业务
            log.error("log record parse exception", t);
        } finally {
            LogRecordContext.clear();
        }
        if (throwable != null) {
            throw throwable;
        }
        return ret;
    }

    /**
     * 记录日志
     */
    private void recordExecute(Object ret, Method method, Object[] args, LogRecordEntity logRecord,
                               Class<?> targetClass, String errorMsg, Operator operator) {
        try {
            boolean emptyUserId = isEmpty(logRecord.getUserId());
            boolean emptyUserName = isEmpty(logRecord.getUserName());
            boolean emptySource = isEmpty(logRecord.getSource());
            logRecord.setError(errorMsg);
            if (emptyUserId) {
                logRecord.setUserId(operator.getUserId());
            }
            if (emptyUserName) {
                logRecord.setUserName(operator.getUserName());
            }
            if (emptySource) {
                logRecord.setSource(properties.getSource());
            }

            // spel解析、需要当前现成的上下文环境
            if (properties.getEnabledSpel()) {
                List<String> spElTemplates = Lists.newArrayList(logRecord.getDetail(),
                        logRecord.getSpare1(), logRecord.getSpare2(), logRecord.getSpare3());
                if (!emptyUserId) {
                    spElTemplates.add(logRecord.getUserId());
                }
                if (!emptyUserName) {
                    spElTemplates.add(logRecord.getUserName());
                }
                Map<String, String> expressionValues = processTemplate(spElTemplates, ret, targetClass, method, args, errorMsg);
                logRecord.setUserId(emptyUserId? logRecord.getUserId() : expressionValues.get(logRecord.getUserId()));
                logRecord.setUserName(emptyUserName? logRecord.getUserName() : expressionValues.get(logRecord.getUserName()));
                logRecord.setDetail(expressionValues.get(logRecord.getDetail()));
                logRecord.setSpare1(expressionValues.get(logRecord.getSpare1()));
                logRecord.setSpare2(expressionValues.get(logRecord.getSpare2()));
                logRecord.setSpare3(expressionValues.get(logRecord.getSpare3()));
            }

            Preconditions.checkNotNull(logRecordService, "logRecordService not init!!");
            logRecordService.save(logRecord);
        } catch (Exception t) {
            log.error("log record execute exception", t);
        }
    }

    private LogRecordEntity collectLogRecord(Method method, LogRecord recordAnnotation) {
        LogRecordEntity logRecordEntity = LogRecordEntity.builder()
                .detail(recordAnnotation.value())
                .userId(recordAnnotation.userId())
                .userName(recordAnnotation.userName())
                .source(recordAnnotation.source())
                .moduleName(recordAnnotation.moduleName())
                .logType(recordAnnotation.logType())
                .spare1(recordAnnotation.spare1())
                .spare2(recordAnnotation.spare2())
                .spare3(recordAnnotation.spare3())
                .createTime(LocalDateTime.now())
                .logStatus(LogStatus.SUCCESS)
                .ip(IPUtils.getIpAddr())
                .url(IPUtils.getRequestUrl())
                .build();
        LogRecord classAnnotation = method.getDeclaringClass().getAnnotation(LogRecord.class);
        if (classAnnotation != null) {
            if (isEmpty(logRecordEntity.getSource())) {
                logRecordEntity.setSource(classAnnotation.source());
            }
            if (isEmpty(logRecordEntity.getModuleName())) {
                logRecordEntity.setModuleName(classAnnotation.moduleName());
            }
            if (isEmpty(logRecordEntity.getSpare1())) {
                logRecordEntity.setSpare1(classAnnotation.spare1());
            }
            if (isEmpty(logRecordEntity.getSpare2())) {
                logRecordEntity.setSpare2(classAnnotation.spare2());
            }
            if (isEmpty(logRecordEntity.getSpare3())) {
                logRecordEntity.setSpare3(classAnnotation.spare3());
            }
        }
        return logRecordEntity;
    }



    private boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    private Class<?> getTargetClass(Object target) {
        return AopProxyUtils.ultimateTargetClass(target);
    }
}
