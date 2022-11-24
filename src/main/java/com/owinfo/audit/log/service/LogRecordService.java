package com.owinfo.audit.log.service;

import com.owinfo.audit.log.config.LogStatus;
import com.owinfo.audit.log.config.LogType;
import com.owinfo.audit.log.service.entity.LogRecordEntity;
import com.owinfo.audit.log.service.entity.Operator;
import com.owinfo.audit.log.service.executor.LogExecutor;
import com.owinfo.audit.log.service.param.QueryParam;
import com.owinfo.audit.log.util.IPUtils;
import com.owinfo.audit.log.util.Page;
import com.owinfo.audit.log.util.Result;
import com.owinfo.audit.log.util.Status;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.*;

/**
 * 日志操作服务
 *
 * @author pengjunjie
 * @date 2022/11/3 13:39
 */
public class LogRecordService {
    private LogExecutor logExecutor;
    private ExecutorService executorService;
    private OperatorService operatorService;

    public LogRecordService(LogExecutor logExecutor, OperatorService operatorService) {
        this.logExecutor = logExecutor;
        this.operatorService = operatorService;
        this.executorService = new ThreadPoolExecutor(10, 100, 60L, TimeUnit.SECONDS,
                new SynchronousQueue(), new LogThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void save(LogRecordEntity recordEntity) {
        executorService.execute(() -> logExecutor.save(recordEntity));
    }

    public void save(String value, String moduleName, LogType logType, LogStatus logStatus) {
        Operator user = operatorService.getUser();
        String ipAddr = IPUtils.getIpAddr();
        String requestUrl = IPUtils.getRequestUrl();
        Assert.notNull(user, "Login User can not be Null");
        executorService.execute(() -> logExecutor.save(value, moduleName, logType, logStatus, user.getUserId(),
                user.getUserName(), ipAddr, requestUrl));
    }

    public Result listLogRecord(QueryParam queryParam) {
        List<LogRecordEntity> logRecordEntities = logExecutor.listLogRecord(queryParam);
        Integer total = logExecutor.countLogRecord(queryParam);
        return Result.build(Status.SUCCESS, logRecordEntities, new Page(total, queryParam.getPage(), queryParam.getPageSize()));
    }

    public static class LogThreadFactory implements ThreadFactory {
        private static int threadInitNumber = 0;

        public LogThreadFactory() {
        }

        private static synchronized int nextThreadNum() {
            return threadInitNumber++;
        }

        public Thread newThread(Runnable r) {
            return new Thread(r, "Log-Record-Service-" + nextThreadNum());
        }
    }
}
