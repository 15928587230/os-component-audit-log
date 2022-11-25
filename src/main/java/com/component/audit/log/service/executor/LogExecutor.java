package com.component.audit.log.service.executor;

import com.component.audit.log.config.LogStatus;
import com.component.audit.log.config.LogType;
import com.component.audit.log.service.entity.LogRecordEntity;
import com.component.audit.log.service.param.QueryParam;

import java.time.LocalDateTime;
import java.util.List;

public interface LogExecutor {

    /**
     * 保存日志
     *
     * @param logRecord
     * @return void
     */
    void save(LogRecordEntity logRecord);

    /**
     * 保存日志
     *
     * @param value
     * @param moduleName
     * @param logType
     * @param logStatus
     */
    void save(String value, String moduleName, LogType logType,
              LogStatus logStatus, String userId, String userName, String ip, String url);

    /**
     * 查看日志列表
     *
     * @param queryParam
     * @return java.util.List<com.owinfo.audit.log.service.entity.LogRecordEntity>
     */
    List<LogRecordEntity> listLogRecord(QueryParam queryParam);

    /**
     * 查询符合条件的日志总数
     *
     * @author pengjunjie
     * @date 2022/11/5 20:43
     * @param queryParam
     * @return java.lang.Integer
     */
    Integer countLogRecord(QueryParam queryParam);

    void initTable(String tableName);

    String getTableName();

    String getTableName(LocalDateTime dateTime);

    String getTableNameNextMonth();
}
