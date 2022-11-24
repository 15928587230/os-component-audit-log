package com.owinfo.audit.log.service.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.owinfo.audit.log.config.LogStatus;
import com.owinfo.audit.log.config.LogType;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LogRecordEntity {

    private String tableName;

    /**
     *  主键
     */
    private Long id;

    /**
     *  来源系统
     */
    private String source;

    /**
     *  用户ID
     */
    private String userId;

    /**
     *  用户名称
     */
    private String userName;

    /**
     *  模块名称
     */
    private String moduleName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     *  Description 操作详情
     */
    private String detail;

    /**
     *  请求URL
     */
    private String url;

    /**
     *  异常信息
     */
    private String error;

    /**
     *  操作类型、新增、删除等等
     */
    private LogType logType;

    /**
     *  成功或者失败, 操作状态
     */
    private LogStatus logStatus;

    /**
     *  客户端IP
     */
    private String ip;

    /**
     *  日志sql语句
     */
    private String sqlString;

    /**
     * 备用1
     */
    private String spare1;

    /**
     * 备用2
     */
    private String spare2;

    /**
     * 备用3
     */
    private String spare3;
}