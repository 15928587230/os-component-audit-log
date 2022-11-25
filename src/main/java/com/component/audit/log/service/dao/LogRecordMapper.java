package com.component.audit.log.service.dao;

import com.component.audit.log.service.entity.LogRecordEntity;
import com.component.audit.log.service.param.QueryParam;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface LogRecordMapper {

    @Insert("insert into ${tableName}(source,user_id,user_name,module_name,create_time," +
            "detail,url,error,log_type,log_status,ip,sql_string,spare1,spare2,spare3) values(#{source},#{userId},#{userName}," +
            "#{moduleName},#{createTime},#{detail},#{url},#{error},#{logType},#{logStatus},#{ip},#{sqlString}," +
            "#{spare1},#{spare2},#{spare3} )")
    void save(@Param("logRecord") LogRecordEntity logRecord);

    @Select({
            "<script>" +
            "SELECT" +
                    " id,source,user_id as userId,user_name as userName,module_name " +
                    " as moduleName,create_time as createTime,detail,url,error," +
                    " log_type as logType, log_status as logStatus,ip,spare1,spare2,spare3" +
                    " FROM" +
                    " ${tableName}" +
                    " <where>" +
                    "<if test=\"startTime != null\">" +
                    " and create_time &gt;= #{startTime}" +
                    "</if>" +
                    "<if test=\"endTime != null\">" +
                    " and create_time &lt; #{endTime}" +
                    "</if>" +
                    "<if test=\"source != null and source != ''\">" +
                    " and source like concat('%', #{source}, '%')" +
                    "</if>" +
                    "<if test=\"userName != null and userName != ''\">" +
                    " and user_name like concat('%', #{userName}, '%')" +
                    "</if>" +
                    "<if test=\"moduleName != null and moduleName != ''\">" +
                    " and module_name like concat('%', #{moduleName}, '%')" +
                    "</if>" +
                    "<if test=\"ip != null and ip != ''\">" +
                    " and ip like concat('%', #{ip}, '%')" +
                    "</if>" +
                    "<if test=\"detail != null and detail != ''\">" +
                    " and detail like concat('%', #{detail}, '%')" +
                    "</if>" +
                    "<if test=\"status != null and status != ''\">" +
                    " and log_status like concat('%', #{status}, '%')" +
                    "</if>" +
                    "</where>" +
                    " ORDER BY" +
                    " id desc" +
                    " offset ${pageIndex} row FETCH NEXT ${pageSize} row ONLY" +
            "</script>"
    })
    List<LogRecordEntity> listLogRecord(@Param("query") QueryParam queryParam);

    @Select({
            "<script>" +
                    "SELECT count(1) from (" +
                    " SELECT" +
                    " top 100 percent" +
                    " id " +
                    " FROM" +
                    " ${tableName}" +
                    " <where>" +
                    "<if test=\"startTime != null\">" +
                    " and create_time &gt;= #{startTime}" +
                    "</if>" +
                    "<if test=\"endTime != null\">" +
                    " and create_time &lt; #{endTime}" +
                    "</if>" +
                    "<if test=\"source != null and source != ''\">" +
                    " and source like concat('%', #{source}, '%')" +
                    "</if>" +
                    "<if test=\"userName != null and userName != ''\">" +
                    " and user_name like concat('%', #{userName}, '%')" +
                    "</if>" +
                    "<if test=\"moduleName != null and moduleName != ''\">" +
                    " and module_name like concat('%', #{moduleName}, '%')" +
                    "</if>" +
                    "<if test=\"ip != null and ip != ''\">" +
                    " and ip like concat('%', #{ip}, '%')" +
                    "</if>" +
                    "<if test=\"detail != null and detail != ''\">" +
                    " and detail like concat('%', #{detail}, '%')" +
                    "</if>" +
                    "<if test=\"status != null and status != ''\">" +
                    " and log_status like concat('%', #{status}, '%')" +
                    "</if>" +
                    "</where>" +
                    " ORDER BY" +
                    " id desc) total" +
                    "</script>"
    })
    Integer countLogRecord(@Param("query") QueryParam queryParam);
}
