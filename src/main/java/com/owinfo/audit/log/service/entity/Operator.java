package com.owinfo.audit.log.service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Operator {
    private String userId;
    private String userName;
    /**
     * 默认会记录日志，当Operator为Null或者recordOrNot=false则不保存日志
     */
    private boolean recordOrNot = true;
}