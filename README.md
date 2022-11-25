# 日志组件, 可扩展存储以及查询接口

公共审计日志组件，通过注解收集日志，保存到数据库，并提供接口查询。

## 使用方式

需要自行打包,然后传到Nexus, 再引入依赖。执行 package即可打包。

Maven

```
    <dependency>
            <groupId>com.owinfo</groupId>
            <artifactId>owinfo-audit-log</artifactId>
            <version>1.0.0</version>
    </dependency>
```

#### SpringBoot入口打开开关,添加 @EnableLogRecord 注解

source是系统的标识，一般为一个子系统的名字

```java
@SpringBootApplication
@EnableLogRecord(source = "订单子系统")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

## 配置文件

```yaml
owinfo:
  audit-log:
    enabled: true
      # 按月分表、表的前缀。表名举例 t_apis_log_dev_2022_11
    # 通过表名把开发和测试环境区分开来, source用来区分不同的系统
    tableName: t_apis_log_dev
    # 是否支持spel解析参数
    enabledSpel: true
    database: SQLServer
    # 是否按月分表
    monthlySubTable: true
    # 单独的数据源
    datasource:
      host: 192.168.0.123
      port: 1433
      database: apis_log
      username: apis_log
      password: apis_log
```

### 配置获取用户信息方法

```java

@Configuration
public class LogOperatorConfig implements LogOperatorConfiguration {

    @Override
    public Operator getUser() {
        //TODO 实现获取上下文用户信息
        User user = UserContext.getUserInfo();
        Operator operator = new Operator(user.getUserId(), user.getUsername());
        return operator;
    }
}

```

## 使用

**@LogRecord** 可以注解在方法上或类上，**同时存在就近原则 `方法上注解` 优先于 `类上注解`**。


|    属性    |       备注       |
| :--------: | :--------------: |
|   value    | 操作详情主要注解 |
|   source   |     来源系统     |
| moduleName |     模块名称     |
|  logType   |     操作类型     |
|  userName  |   操作人用户名   |
|   userId   |     操作人id     |
|   detail   |     操作详情     |
|            |                  |

### 可用变量

- 入参
- 出参
- 返回值: #_ret变量
- 异常的错误信息: #_errorMsg
- {{#currentUser}}
- 还可以通过SpEL的 T 方式调用静态方法

### 最简使用

```java

@RestController
public class ApiTestController {

    @LogRecord("访问了测试接口")
    @RequestMapping("/api/test")
    public String test(String param) {

        return "结果";
    }
}

```

### 基本使用

SpEL 表达式：其中用双大括号包围起来的（例如：{{#order.purchaseName}}）#order.purchaseName 是 SpEL表达式。Spring中支持的它都支持的。比如调用静态方法，三目表达式。SpEL 可以使用方法中的任何参数

```java
   /**'张三下了一个订单,购买商品「超值优惠红烧肉套餐」,下单结果:true' */

    @LogRecord(
            value = "{{#order.purchaseName}}下了一个订单,购买商品「{{#order.productName}}」,下单结果:{{#_ret}}",
            moduleName = "订单模块",
            logType = LogType.ADD,
            detail = "{{#order.toString()}}"
    )
    @PostMapping("/api/order")
    public String test2(Order order) {
        //下单
        return "结果";
    }
```

### 提高日志分页查询接口、不支持跨月查询

```java
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
```

**api日志分页查询接口**

```java
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
```
