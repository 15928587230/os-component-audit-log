package com.owinfo.audit.log.controller;

import com.owinfo.audit.log.service.LogRecordService;
import com.owinfo.audit.log.service.param.QueryParam;
import com.owinfo.audit.log.util.Page;
import com.owinfo.audit.log.util.Result;
import com.owinfo.audit.log.util.Status;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;

@RestController
public class MonthLogController {

    @Resource
    private LogRecordService recordService;

    @PostMapping("/listMonthLogRecords")
    public Result listMonthLog(@RequestBody QueryParam queryParam) {
        Result valid = valid(queryParam);
        if (!valid.isSuccess()) {
            return valid;
        }

        try {
            return recordService.listLogRecord(queryParam);
        } catch (Exception ignored) {
        }
        return Result.build(Status.SUCCESS, new ArrayList<>(), new Page(0, queryParam.getPage(), queryParam.getPageSize()));
    }

    private Result valid(QueryParam queryParam) {
        String msg = "";
        if (queryParam.getPage() == null
                || queryParam.getPageSize() == null
                || queryParam.getStartTime() == null
                || queryParam.getEndTime() == null) {
            msg = "page、pageSize、startTime、endTime 不能为空";
            return Result.build(Status.FAILURE, msg);
        }

        if (queryParam.getPage() < 0) {
            queryParam.setPage(1);
        }
        if (queryParam.getPageSize() > 100) {
            queryParam.setPageSize(100);
        }
        // 设置查询得index位置
        queryParam.setPageIndex((queryParam.getPage() - 1) * queryParam.getPageSize());

        LocalDate startDate = queryParam.getStartTime().toLocalDate();
        LocalDate endDate = queryParam.getEndTime().toLocalDate();
        int startDateYear = startDate.getYear();
        int startDateMonth = startDate.getMonth().getValue();
        int endDateYear = endDate.getYear();
        int endDateMonth = endDate.getMonth().getValue();
        boolean sameYearMonth = startDateYear == endDateYear && startDateMonth == endDateMonth;
        if (!sameYearMonth) {
            msg = "开始日期和结束日期只能在同一个月之内";
            return Result.build(Status.FAILURE, msg);
        }

        // 结束时间 yyyy-MM-dd 00:00:00 加一天
        queryParam.setEndTime(queryParam.getEndTime().plusDays(1));
        return Result.success();
    }
}
