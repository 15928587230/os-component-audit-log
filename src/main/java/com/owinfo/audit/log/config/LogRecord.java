package com.owinfo.audit.log.config;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LogRecord {

    /**
     * 系统名
     *
     * @return
     */
    String source() default "";


    /**
     * 模块名称
     */
    String moduleName() default "";

    /**
     * 操作
     *
     * @return
     */
    String value() default "";

    /**
     * 操作人用户名
     *
     * @return
     */
    String userName() default "";

    /**
     * 操作类型、新增、删除等等
     *
     * @return
     */
    LogType logType() default LogType.QUERY;

    /**
     * 操作人id
     *
     * @return
     */
    String userId() default "";


    /**
     * 备用1
     */
    String spare1() default "";

    /**
     * 备用2
     */
    String spare2() default "";

    /**
     * 备用3
     */
    String spare3() default "";
}
