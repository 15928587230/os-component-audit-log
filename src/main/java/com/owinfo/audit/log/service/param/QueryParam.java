package com.owinfo.audit.log.service.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QueryParam {
    private String tableName;
    /**
     * 来源系统
     */
    private String source;
    private Integer pageIndex;
    private Integer page;
    private Integer pageSize;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;
    private String userName;
    private String moduleName;
    private String ip;
    private String detail;
    private String status;
}
