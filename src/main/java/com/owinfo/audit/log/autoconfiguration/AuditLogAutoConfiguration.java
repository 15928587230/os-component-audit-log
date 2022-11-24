package com.owinfo.audit.log.autoconfiguration;


import com.owinfo.audit.log.aop.LogRecordAdvisor;
import com.owinfo.audit.log.aop.LogRecordInterceptor;
import com.owinfo.audit.log.config.DatabaseType;
import com.owinfo.audit.log.config.EnableLogRecord;
import com.owinfo.audit.log.service.LogRecordService;
import com.owinfo.audit.log.service.OperatorService;
import com.owinfo.audit.log.service.OperatorServiceImpl;
import com.owinfo.audit.log.service.dao.InitTableMapper;
import com.owinfo.audit.log.service.dao.LogRecordMapper;
import com.owinfo.audit.log.service.executor.LogExecutor;
import com.owinfo.audit.log.service.executor.SqlserverExecutor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import javax.sql.DataSource;

@Slf4j
@ComponentScan("com.owinfo.audit.log")
@Configuration
@EnableConfigurationProperties(AuditLogProperties.class)
@ConditionalOnProperty(prefix = "owinfo.audit-log", name = "enabled",
        havingValue = "true", matchIfMissing = false)
public class AuditLogAutoConfiguration implements ImportAware {
    final AuditLogProperties properties;
    final SqlSessionFactory sqlSessionFactory;
    private AnnotationAttributes enableLogRecord;

    public AuditLogAutoConfiguration(AuditLogProperties properties) {
        this.properties = properties;
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, createNewDataSource());
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration(environment);
        configuration.addMapper(LogRecordMapper.class);
        configuration.addMapper(InitTableMapper.class);
        this.sqlSessionFactory = (new SqlSessionFactoryBuilder()).build(configuration);
    }

    /**
     * 初始化日志记录Service
     *
     * @return
     */
    @Bean
    public LogRecordService logRecordService(LogExecutor logExecutor, OperatorService operatorService) {
        if (enableLogRecord != null) {
            properties.setSource(enableLogRecord.getString("source"));
        }
        return new LogRecordService(logExecutor, operatorService);
    }

    @Bean
    public LogExecutor sqlExecutor(OperatorService operatorService) {
        if (properties.getDatabase() == DatabaseType.SQLServer) {
            return new SqlserverExecutor(properties, this.sqlSessionFactory);
        }
        return null;
    }

    @Bean
    public LogRecordAdvisor logRecordAdvisor(LogRecordInterceptor logRecordInterceptor) {
        return new LogRecordAdvisor(logRecordInterceptor);
    }

    @Bean
    public LogRecordInterceptor logRecordInterceptor(OperatorService operatorService,
                                                     AuditLogProperties properties,
                                                     LogRecordService logRecordService) {
        return new LogRecordInterceptor(operatorService, properties, logRecordService);
    }

    @Bean
    @ConditionalOnMissingBean(OperatorService.class)
    public OperatorService operatorService() {
        return new OperatorServiceImpl();
    }

    /**
     * 创建数据源
     */
    public DataSource createNewDataSource() {
        log.info("initing LogRecord DataSource");

        AuditLogProperties.DataSourceConfig dataSourceConfig = properties.getDatasource();

        if (StringUtils.isAnyEmpty(dataSourceConfig.getHost(),
                dataSourceConfig.getPort(),
                dataSourceConfig.getUsername(),
                dataSourceConfig.getPassword(),
                dataSourceConfig.getDatabase())) {
            throw new IllegalArgumentException("owinfo.audit-log.datasource 参数不能为空");
        }

        String mysql = "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowMultiQueries=true&useAffectedRows=true";
        String sqlserver = "jdbc:sqlserver://%s:%s;DatabaseName=%s";
        String url = mysql;
        if (DatabaseType.SQLServer.equals(properties.getDatabase())) {
            url = sqlserver;
        }
        String jdbcurl = String.format(url, dataSourceConfig.getHost(), dataSourceConfig.getPort(), dataSourceConfig.getDatabase());

        // 更多属性参考官网配置
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcurl);
        config.setUsername(dataSourceConfig.getUsername());
        config.setPassword(dataSourceConfig.getPassword());
        return new HikariDataSource(config);
    }

    @Override
    public void setImportMetadata(AnnotationMetadata annotationMetadata) {
        this.enableLogRecord = AnnotationAttributes.fromMap(
                annotationMetadata.getAnnotationAttributes(EnableLogRecord.class.getName(), false));
        if (this.enableLogRecord == null) {
            log.warn("@EnableLogRecord is not present on applcation class");
        }
    }
}
