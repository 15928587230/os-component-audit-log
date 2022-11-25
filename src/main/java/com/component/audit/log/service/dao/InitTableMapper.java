package com.component.audit.log.service.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface InitTableMapper {

    @Update("IF NOT EXISTS (SELECT * FROM sys.objects \n" +
            "WHERE object_id = OBJECT_ID(N'[dbo].[${_parameter}]') AND type in (N'U'))\n" +
            "BEGIN\n" +
            "CREATE TABLE ${_parameter} (\n" +
            "  [id] bigint NOT NULL PRIMARY KEY IDENTITY(1,1),\n" +
            "  [create_time] datetime2(0)  NOT NULL,\n" +
            "  [user_id] nvarchar(64) COLLATE Chinese_PRC_CI_AS  NOT NULL,\n" +
            "  [user_name] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,\n" +
            "  [source] nvarchar(32) COLLATE Chinese_PRC_CI_AS  NULL,\n" +
            "  [module_name] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,\n" +
            "  [url] nvarchar(512) COLLATE Chinese_PRC_CI_AS  NULL,\n" +
            "  [error] nvarchar(max) COLLATE Chinese_PRC_CI_AS  NULL,\n" +
            "  [ip] nvarchar(32) COLLATE Chinese_PRC_CI_AS NULL,\n" +
            "  [log_type] nvarchar(20) COLLATE Chinese_PRC_CI_AS NULL,\n" +
            "  [detail] nvarchar(max) COLLATE Chinese_PRC_CI_AS  NULL,\n" +
            "  [log_status] nvarchar(20) COLLATE Chinese_PRC_CI_AS  NULL,\n" +
            "  [sql_string] nvarchar(max) COLLATE Chinese_PRC_CI_AS  NULL,\n" +
            "  [spare1] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,\n" +
            "  [spare2] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL,\n" +
            "  [spare3] nvarchar(255) COLLATE Chinese_PRC_CI_AS  NULL\n" +
            ");\n" +
            "CREATE NONCLUSTERED INDEX create_time_index ON ${_parameter}(create_time);\n" +
            "END")
    void initSqlserverTable(@Param("tableName") String tableName);
}
