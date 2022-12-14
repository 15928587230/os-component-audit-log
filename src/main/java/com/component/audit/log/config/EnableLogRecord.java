package com.component.audit.log.config;

import com.component.audit.log.autoconfiguration.AuditLogAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AuditLogAutoConfiguration.class)
public @interface EnableLogRecord {
    String source();
}
