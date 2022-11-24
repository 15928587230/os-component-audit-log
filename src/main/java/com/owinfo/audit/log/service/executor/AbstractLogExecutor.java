package com.owinfo.audit.log.service.executor;

import com.owinfo.audit.log.autoconfiguration.AuditLogProperties;
import org.apache.ibatis.session.SqlSessionFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class AbstractLogExecutor implements LogExecutor {

    protected AuditLogProperties properties;
    protected SqlSessionFactory sqlSessionFactory;

    public AbstractLogExecutor(AuditLogProperties properties,
                               SqlSessionFactory sqlSessionFactory) {
        this.properties = properties;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    /**
     * 根据情况获取表名
     *
     * @return
     */
    @Override
    public String getTableName() {
        return getTableName(LocalDateTime.now());
    }

    @Override
    public String getTableNameNextMonth() {
        return getTableName(LocalDateTime.now().plusMonths(1));
    }

    @Override
    public String getTableName(LocalDateTime dateTime) {
        if (dateTime == null) {
            return getTableName();
        }
        return properties.getTableName().replaceAll("`", "") + getSuffix(dateTime);
    }

    /**
     * @return 表名后缀 _2020_04
     */
    public String getSuffix(LocalDateTime localDateTime) {
        if (properties.getMonthlySubTable()) {
            DateTimeFormatter outFormat = DateTimeFormatter.ofPattern("_yyyy_MM");
            String outDateStr = localDateTime.format(outFormat);
            return outDateStr;
        }
        return "";
    }
    
}
