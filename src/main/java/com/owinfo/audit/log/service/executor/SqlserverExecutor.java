package com.owinfo.audit.log.service.executor;

import com.owinfo.audit.log.autoconfiguration.AuditLogProperties;
import com.owinfo.audit.log.config.LogStatus;
import com.owinfo.audit.log.config.LogType;
import com.owinfo.audit.log.service.entity.LogRecordEntity;
import com.owinfo.audit.log.service.param.QueryParam;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

public class SqlserverExecutor extends AbstractLogExecutor {
    public SqlserverExecutor(AuditLogProperties properties,
                             SqlSessionFactory sqlSessionFactory) {
        super(properties, sqlSessionFactory);
    }

    @Override
    public void save(LogRecordEntity logRecord) {
        SqlSession sqlSession = this.sqlSessionFactory.openSession();
        try {
            logRecord.setTableName(getTableName());
            sqlSession.insert("com.owinfo.audit.log.service.dao.LogRecordMapper.save", logRecord);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void save(String value, String moduleName, LogType logType, LogStatus logStatus,
                     String userId, String userName, String ip, String url) {
        LogRecordEntity logRecordEntity = LogRecordEntity.builder()
                .detail(value)
                .userId(userId)
                .userName(userName)
                .source(properties.getSource())
                .moduleName(moduleName)
                .logType(logType)
                .createTime(LocalDateTime.now())
                .logStatus(logStatus)
                .ip(ip)
                .url(url)
                .build();
        this.save(logRecordEntity);
    }

    @Override
    public List<LogRecordEntity> listLogRecord(QueryParam queryParam) {
        SqlSession sqlSession = this.sqlSessionFactory.openSession(false);
        try {
            Assert.notNull(queryParam.getStartTime(), "StartTime can not be NULL");
            queryParam.setTableName(getTableName(queryParam.getStartTime()));
            return sqlSession.selectList("com.owinfo.audit.log.service.dao.LogRecordMapper.listLogRecord", queryParam);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public Integer countLogRecord(QueryParam queryParam) {
        SqlSession sqlSession = this.sqlSessionFactory.openSession(false);
        try {
            Assert.notNull(queryParam.getStartTime(), "StartTime can not be NULL");
            queryParam.setTableName(getTableName(queryParam.getStartTime()));
            return sqlSession.selectOne("com.owinfo.audit.log.service.dao.LogRecordMapper.countLogRecord", queryParam);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public void initTable(String tableName) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            sqlSession.update("com.owinfo.audit.log.service.dao.InitTableMapper.initSqlserverTable", tableName);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }
}
