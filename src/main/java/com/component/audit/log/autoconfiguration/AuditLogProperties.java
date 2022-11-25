package com.component.audit.log.autoconfiguration;

import com.component.audit.log.config.DatabaseType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;


@Data
@ConfigurationProperties(prefix = "owinfo.audit-log")
public class AuditLogProperties {

    /**
     * 开启框架 默认 true
     */
    Boolean enabled = true;

    Boolean enabledSpel = false;

    private String tableName;

    /**
     *  来源系统
     */
    private String source;

    /**
     * 数据库类型 默认MySQL 支持： MySQL， sqlserver
     */
    DatabaseType database = DatabaseType.MySQL;

    Boolean recordSql = false;
    /**
     * 打印sql 默认false
     */
    Boolean showSql = false;

    /**
     *  按月分表
     */
    private Boolean monthlySubTable = false;

    /**
     * 数据源
     */
    DataSourceConfig datasource;

    @Data
    public static class DataSourceConfig {

        String host;

        String port;

        String database;

        String username;

        String password;
    }
}
