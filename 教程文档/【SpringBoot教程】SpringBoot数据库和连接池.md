# Springboot整合Mysql和连接池(druid)

## Springboot2





本文记录了 springboot配置 druid数据源的步骤；

------

## 【1】新建springboot项目并配置druid

步骤1，新建springbt项目

![img](https://i-blog.csdnimg.cn/blog_migrate/f44a7a1100e5b40dcb2ebd7a81e749ef.png)

 步骤2，选择spring web，jdbc，mysql驱动依赖；

![img](https://i-blog.csdnimg.cn/blog_migrate/5b34b3183912f32a3c98a5e99bdbc73a.png)

步骤3，添加 druid数据源依赖， 生成的pom.xml 如下：

```XML
<?xml version="1.0" encoding="UTF-8"?>



<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"



         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">



    <modelVersion>4.0.0</modelVersion>



    <parent>



        <groupId>org.springframework.boot</groupId>



        <artifactId>spring-boot-starter-parent</artifactId>



        <version>2.5.5</version>



        <relativePath/> <!-- lookup parent from repository -->



    </parent>



    <groupId>com.cmc</groupId>



    <artifactId>springbt-06-data-jdbc2</artifactId>



    <version>0.0.1-SNAPSHOT</version>



    <name>springbt-06-data-jdbc2</name>



    <description>Demo project for Spring Boot</description>



    <properties>



        <java.version>1.8</java.version>



    </properties>



 



    <dependencies>



        <dependency>



            <groupId>org.springframework.boot</groupId>



            <artifactId>spring-boot-starter-jdbc</artifactId>



        </dependency>



        <dependency>



            <groupId>org.springframework.boot</groupId>



            <artifactId>spring-boot-starter-web</artifactId>



        </dependency>



 



        <dependency>



            <groupId>mysql</groupId>



            <artifactId>mysql-connector-java</artifactId>



            <scope>runtime</scope>



        </dependency>



 



        <!-- 引入druid数据源 -->



        <dependency>



            <groupId>com.alibaba</groupId>



            <artifactId>druid</artifactId>



            <version>1.2.8</version>



        </dependency>



        <!-- https://mvnrepository.com/artifact/log4j/log4j -->



        <dependency>



            <groupId>log4j</groupId>



            <artifactId>log4j</artifactId>



            <version>1.2.17</version>



        </dependency>



 



        <dependency>



            <groupId>org.springframework.boot</groupId>



            <artifactId>spring-boot-starter-test</artifactId>



            <scope>test</scope>



        </dependency>



    </dependencies>



 



    <build>



        <plugins>



            <plugin>



                <groupId>org.springframework.boot</groupId>



                <artifactId>spring-boot-maven-plugin</artifactId>



            </plugin>



        </plugins>



    </build>



 



</project>
```

注意，必须添加 log4j 依赖，因为 druid用到了它，不然会报

```cpp
Failed to bind properties under 'spring.datasource' to javax.sql.DataSource
```

步骤4，添加druid数据源配置， 启用 druid数据源

application.yml

```XML
# 配置springboot数据源



spring:



  datasource:



    username: root



    password: root



    url: jdbc:mysql://192.168.163.204:3306/jdbc01



    driver-class-name: com.mysql.cj.jdbc.Driver



    type: com.alibaba.druid.pool.DruidDataSource  # 启用druid数据源



    #   数据源其他配置



    initialSize: 6



    minIdle: 6



    maxActive: 26



    maxWait: 60000



    timeBetweenEvictionRunsMillis: 60000



    minEvictableIdleTimeMillis: 300000



    testWhileIdle: true



    testOnBorrow: false



    testOnReturn: false



    poolPreparedStatements: true



    #   配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙



    filters: stat,wall,log4j



    maxPoolPreparedStatementPerConnectionSize: 20



    useGlobalDataSourceStat: true



    connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500
```

步骤5，添加测试用例

```java
import org.junit.jupiter.api.Test;



import org.springframework.beans.factory.annotation.Autowired;



import org.springframework.boot.test.context.SpringBootTest;



 



import javax.sql.DataSource;



import java.sql.Connection;



import java.sql.SQLException;



 



@SpringBootTest



class Springbt06DataJdbc2ApplicationTests {



 



    @Autowired



    DataSource dataSource;



 



    @Test



    void contextLoads() throws SQLException {



        System.out.println("数据源=" + dataSource.getClass());



        Connection conn =  dataSource.getConnection();



        System.out.println("我的测试连接=" + conn);



        conn.close();



    }



}
```

打印结果：

> 数据源=class com.alibaba.druid.pool.DruidDataSource
> 2021-10-17 08:18:50.867 INFO 7488 --- [      main] com.alibaba.druid.pool.DruidDataSource  : {dataSource-1} inited
> 我的测试连接=com.mysql.cj.jdbc.ConnectionImpl@54e02f6a

------

##  【2】使用druid并配置druid监控页面

步骤1，添加web访问配置

application.properties

```XML
# 服务器配置



server.port=8082



server.servlet.context-path=/springbt-data2
```

步骤2，添加controller访问路径，查询数据库表

```java
@Controller



public class HelloController {



    @Autowired



    JdbcTemplate jdbcTemplate;



 



    @ResponseBody



    @GetMapping("/query")



    public Map<String, Object> map() {



        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from department");



        return list.get(0);



    }



}
```

步骤3，添加druid数据源

```java
// 导入druid数据源



@Configuration



public class DruidConfig {



 



    @ConfigurationProperties(prefix = "spring.datasource")



    @Bean



    public DataSource druid() {



        return new DruidDataSource();



    }



 



    // 配置druid监控



    // 1 配置一个管理后台的servlet



    @Bean



    public ServletRegistrationBean statViewServlet() {



        ServletRegistrationBean bean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");



        // 配置相关参数



        Map<String, String> params = new HashMap<>();



        params.put("loginUsername", "admin");



        params.put("loginPassword", "admin");



        params.put("allow", "localhost"); // 默认允许所有访问



        params.put("deny", "192.168.163.204"); // 默认允许所有访问



        bean.setInitParameters(params);



        return bean;



    }



 



    // 2 配置一个监控的filter



    @Bean



    public FilterRegistrationBean webStatFilter() {



        FilterRegistrationBean bean = new FilterRegistrationBean<>();



        bean.setFilter(new WebStatFilter());



        // 配置相关参数



        Map<String, String> params = new HashMap<>();



        params.put("exclusions", "*.js,*.css,/druid/*");



        bean.setInitParameters(params);



        bean.setUrlPatterns(Arrays.asList("/*"));



        return bean;



    }



}
```

步骤4，先执行query 请求；

http://localhost:8082/springbt-data2/query

![img](https://i-blog.csdnimg.cn/blog_migrate/0a8bad37e9fc95f85b9dc8669f5e912d.png)



步骤5，查看druid sql监控页面

http://localhost:8082/springbt-data2/druid/login.html

 

![img](https://i-blog.csdnimg.cn/blog_migrate/c1fae9a3a96c995c15a72117ff3cf716.png)

步骤6，查看sql监控； 

![img](https://i-blog.csdnimg.cn/blog_migrate/2affcc24279f491cc3f8b7f6741c6820.png)



补充： springboot配置druid及测试案例的目录结构 

![img](https://i-blog.csdnimg.cn/blog_migrate/97026224cbeb64eb750ae0931b7a3daf.png)









## Springboot3

### 实践案例

#### 1、要求说明

> 访问本地数据库ry-vue中sys_user表中对应的字段。

#### 2、具体实现

①在项目pom文件中引入以下依赖

> ```
> <parent>
>  <groupId>org.springframework.boot</groupId>
>  <artifactId>spring-boot-starter-parent</artifactId>
>  <version>3.0.5</version>
> </parent>
> 
> <dependencies>
>  <dependency>
>      <groupId>org.springframework.boot</groupId>
>      <artifactId>spring-boot-starter-web</artifactId>
>  </dependency>
> 
>  <!--数据库相关配置启动器 jdbctemplate 事务相关-->
>  <dependency>
>      <groupId>org.springframework.boot</groupId>
>      <artifactId>spring-boot-starter-jdbc</artifactId>
>  </dependency>
> 
>  <!--druid启动器的依赖-->
>  <dependency>
>      <groupId>com.alibaba</groupId>
>      <artifactId>druid-spring-boot-3-starter</artifactId>
>      <version>1.2.18</version>
>  </dependency>
> 
>  <!--驱动类-->
>  <dependency>
>      <groupId>mysql</groupId>
>      <artifactId>mysql-connector-java</artifactId>
>      <version>8.0.28</version>
>  </dependency>
> 
>  <dependency>
>      <groupId>org.projectlombok</groupId>
>      <artifactId>lombok</artifactId>
>      <version>1.18.28</version>
>  </dependency>
> </dependencies>
> ```

②创建application.[yaml配置文件](https://so.csdn.net/so/search?q=yaml配置文件&spm=1001.2101.3001.7020)，并在文件中配置以下相关属性

> ```
> spring:
> datasource:
>  type: com.alibaba.druid.pool.DruidDataSource # 使用druid连接池
>  #username: 账号                 如果使用springboot2整合druid连接池！使用此信息配置账号、密码！！！！！！！
>  #password:                     但是springboot3不支持该配置方式！！！！！！！！！！！！！！！！
> 
>  # Druid的其他属性配置
>  druid:
>    # 初始化时建立物理连接的个数
>    initial-size: 5
>    # 连接池的最小空闲数量
>    min-idle: 5
>    # 连接池最大连接数量
>    max-active: 20
>    # 获取连接时最大等待时间，单位毫秒
>    max-wait: 60000
>    # 申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
>    test-while-idle: true
>    # 既作为检测的间隔时间又作为testWhileIdel执行的依据
>    time-between-eviction-runs-millis: 60000
>    # 销毁线程时检测当前连接的最后活动时间和当前时间差大于该值时，关闭当前连接(配置连接在池中的最小生存时间)
>    min-evictable-idle-time-millis: 30000
>    # 用来检测数据库连接是否有效的sql 必须是一个查询语句(oracle中为 select 1 from dual)
>    validation-query: select 1
>    # 申请连接时会执行validationQuery检测连接是否有效,开启会降低性能,默认为true
>    test-on-borrow: false
>    # 归还连接时会执行validationQuery检测连接是否有效,开启会降低性能,默认为true
>    test-on-return: false
>    # 是否缓存preparedStatement, 也就是PSCache,PSCache对支持游标的数据库性能提升巨大，比如说oracle,在mysql下建议关闭。
>    pool-prepared-statements: false
>    # 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。在Druid中，不会存在Oracle下PSCache占用内存过多的问题，可以把这个数值配置大一些，比如说100
>    max-pool-prepared-statement-per-connection-size: -1
>    # 合并多个DruidDataSource的监控数据
>    use-global-data-source-stat: true
>    url: jdbc:mysql://localhost:3306/ry-vue    # ！！！！！！！！！！这个是本地sql表的位置和名称
>    username: root    # 数据库账号！！！！！！！！！！！！！！！
>    password: aishangfei34.    #数据库密码！！！！！！！！！！！！！！！
>    driver-class-name: com.mysql.cj.jdbc.Driver
> ```

③在根包下创建启动类Main，并在该类上添加**注解@SpringBootApplication**，写处启动类的默认启动代码

**④在根包下创建pojo包，在该包下创建User类，添加要读出sql数据库中对应表的字段作为属性，并给该类添加@Data注解**

⑤在根包下创建controller包，在该包下创建userController类，**给该类加上注解：@RestController和@RequestMapping（”路由映射名“）**

**⑥在userController类中写以下代码**

> ```
> @RestController
> @RequestMapping("user")
> public class UserController {
> 
>  @Autowired #！！！！！！！！！！！！！！！！！！！！！！！！！
>  private JdbcTemplate jdbcTemplate; #！！！！！！！！！！！！！！！！！！！！！！！！
>  @GetMapping("list")
>  public List<User> list(){
>      //数据库
>      String sql = "select user_id,dept_id,user_name,email from sys_user;"; //查询语句，查询字段要和User类属性对应！！！！！！！！！！！！！！！！！
>      List<User> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
>      return list;
>  }
> }
> ```

⑦由于springboot3还不兼容druid，所以需要在**resources文件下创建META-INF/spring目录**，在该目录下导入**org.springframework.boot.autoconfigure.AutoConfiguration.imports**文件，文件内容如下：

> ```
> com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure
> ```

#### 3、遇到的问题 

①sql语句中只需要写上实体类中对应的属性名，属性名和sql语句的查询字段必须**对应**上。

②引入JdbcTemplate时，要加上**@Autowired**注解。



## [SpringBoot3整合Druid数据源的解决方案](https://www.cnblogs.com/lisong0626/p/18054597)

druid-spring-boot-3-starter目前最新版本是1.2.20，虽然适配了SpringBoot3，但缺少自动装配的配置文件，会导致加载时报加载驱动异常。

```xml
<dependency>
   <groupId>com.alibaba</groupId>
   <artifactId>druid-spring-boot-3-starter</artifactId>
   <version>1.2.20</version>
</dependency>
```

### 解决方案

需要手动在resources目录下创建`META-INF/spring/`目录，并且在`META-INF/spring/`创建 `org.springframework.boot.autoconfigure.AutoConfiguration.imports`,
![image](https://img2024.cnblogs.com/blog/3382744/202403/3382744-20240305175937010-1908040584.png)
文件中添加如下内容：

```avrasm
com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure
```

### application.yaml

> 添加druid连接池配置项

```yaml
spring:
  datasource:
    # 连接池类型 
    type: com.alibaba.druid.pool.DruidDataSource
 
    # Druid的其他属性配置 springboot3整合情况下,数据库连接信息必须在Druid属性下!
    druid:
      url: jdbc:mysql://localhost:3306/day01
      username: root
      password: root
      driver-class-name: com.mysql.cj.jdbc.Driver
      # 初始化时建立物理连接的个数
      initial-size: 5
      # 连接池的最小空闲数量
      min-idle: 5
      # 连接池最大连接数量
      max-active: 20
      # 获取连接时最大等待时间，单位毫秒
      max-wait: 60000
      # 申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
      test-while-idle: true
      # 既作为检测的间隔时间又作为testWhileIdel执行的依据
      time-between-eviction-runs-millis: 60000
      # 销毁线程时检测当前连接的最后活动时间和当前时间差大于该值时，关闭当前连接(配置连接在池中的最小生存时间)
      min-evictable-idle-time-millis: 30000
      # 用来检测数据库连接是否有效的sql 必须是一个查询语句(oracle中为 select 1 from dual)
      validation-query: select 1
      # 申请连接时会执行validationQuery检测连接是否有效,开启会降低性能,默认为true
      test-on-borrow: false
      # 归还连接时会执行validationQuery检测连接是否有效,开启会降低性能,默认为true
      test-on-return: false
      # 是否缓存preparedStatement, 也就是PSCache,PSCache对支持游标的数据库性能提升巨大，比如说oracle,在mysql下建议关闭。
      pool-prepared-statements: false
      # 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。在Druid中，不会存在Oracle下PSCache占用内存过多的问题，可以把这个数值配置大一些，比如说100
      max-pool-prepared-statement-per-connection-size: -1
      # 合并多个DruidDataSource的监控数据
      use-global-data-source-stat: true
```







