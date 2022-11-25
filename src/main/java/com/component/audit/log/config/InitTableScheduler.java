package com.component.audit.log.config;

import com.component.audit.log.service.executor.LogExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 定时任务初始化表
 */
@Component
public class InitTableScheduler {
    private final LogExecutor logExecutor;

    public InitTableScheduler(LogExecutor logExecutor) {
        this.logExecutor = logExecutor;
    }


    /**
     * 启动执行一次、每天半夜2点执行一次
     */
    @PostConstruct
    @Scheduled(cron = "* * 2 * * ?")
    public void initTable() {
        logExecutor.initTable(logExecutor.getTableName());
        logExecutor.initTable(logExecutor.getTableNameNextMonth());
    }
}
