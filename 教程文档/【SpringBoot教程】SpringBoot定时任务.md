

[toc]





# SpringBoot使用@Scheduled实现定时任务









SpringBoot 实现定时任务很简单，只需要使用@Scheduled注解即可，但是该注解是实现的定时任务默认是单线程的，也就意味着多个定时任务执行时就可能导致线程堵塞，延缓定时任务的执行。所以在需要的时候，我们可以设置一个线程池去执行定时任务。

## 在启动类上加入@EnableScheduling注解



```java
// 启用定时任务
@EnableScheduling 
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```



```java
@Component
public class Task {
    Logger logger = LoggerFactory.getLogger(Task.class);
	// 每五秒执行一次
    @Scheduled(cron = "0/5 * * * * ?")
    public void taskTestA() throws InterruptedException {
        logger.info("A:");
        TimeUnit.SECONDS.sleep(20);
    }
  
    // 每十秒执行一次
    @Scheduled(cron = "0/10 * * * * ?")
    public void taskTestB() {
        logger.info("B:");
    }
}
```

结果：

[![img](https://img2023.cnblogs.com/blog/2443180/202308/2443180-20230807112502678-1640101069.png)](https://img2023.cnblogs.com/blog/2443180/202308/2443180-20230807112502678-1640101069.png)

由图可知，首先这两个定时任务都是单线程的，但是当定时A执行了一次后，由于定时A中有个休眠20秒，然后执行定时任务B，所以线程A第二次执行在25秒后才执行，这就是由于@Scheduled定时任务是单线程，造成的线程堵塞，导致定时任务推迟执行。

## @Scheduled + 配置线程池

（一）在配置文件添加线程池的配置



```yaml
// 若不设置默认为单线程，这里设置使用线程池，大小为4
spring：
  task:
    scheduling:
      pool:
        size: 4
```

（二）配置类配置线程池

通过实现SchedulingConfigurer接口来将定时线程池放入



```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class TaskConfig implements SchedulingConfigurer {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
        executor.setPoolSize(10);
        executor.setThreadNamePrefix("task-thread");
        //设置饱和策略
        //CallerRunsPolicy：线程池的饱和策略之一，当线程池使用饱和后，直接使用调用者所在的线程来执行任务；如果执行程序已关闭，则会丢弃该任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

	//配置@Scheduled 定时器所使用的线程池
	//配置任务注册器：ScheduledTaskRegistrar 的任务调度器
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
    	//可配置两种类型：TaskScheduler、ScheduledExecutorService
        //scheduledTaskRegistrar.setScheduler(taskScheduler());
        //只可配置一种类型：taskScheduler
        scheduledTaskRegistrar.setTaskScheduler(taskScheduler());
    }

}
```

**线程池的饱和策略：**

如果当前同时运行的线程数量达到最大线程数量并且队列也已经被放满了，线程池会执行饱和策略。

- ThreadPoolExecutor.AbortPolicy：抛出 RejectedExecutionException来拒绝新任务的处理。
- ThreadPoolExecutor.CallerRunsPolicy：当线程池使用饱和后，直接使用调用者所在的线程来执行任务；如果执行程序已关闭，则会丢弃该任务。
- ThreadPoolExecutor.DiscardPolicy：不处理新任务，直接丢弃掉。
- ThreadPoolExecutor.DiscardOldestPolicy： 此策略将丢弃最早的未处理的任务请求。

 





# 定时任务@Scheduled用法及其参数讲解

### 1. 基本用法

@[Scheduled](https://so.csdn.net/so/search?q=Scheduled&spm=1001.2101.3001.7020) 由Spring定义，用于将方法设置为调度任务。如：方法每隔十秒钟被执行、方法在固定时间点被执行等

1. @Scheduled(fixedDelay = 1000)
   上一个任务结束到下一个任务开始的时间间隔为固定的1秒，任务的执行总是要先等到上一个任务的执行结束
2. @Scheduled(fixedRate = 1000)
   每间隔1秒钟就会执行任务（如果任务执行的时间超过1秒，则下一个任务在上一个任务结束之后立即执行）
3. @Scheduled(fixedDelay = 1000, initialDelay = 2000)
   第一次执行的任务将会延迟2秒钟后才会启动
4. @Scheduled(cron = “0 15 10 15 * ?”)
   [Cron表达式](https://so.csdn.net/so/search?q=Cron表达式&spm=1001.2101.3001.7020)，每个月的15号上午10点15分开始执行任务
5. 在配置文件中配置任务调度的参数

```java
@Scheduled(fixedDelayString = "fixedDelay.in.milliseconds")
 
@Scheduled(fixedRateString="fixedDelay.in.milliseconds")
 
@Scheduled(fixedRateString="{fixedRate.in.milliseconds}")
 
@Scheduled(cron = "${cron.expression}")
```

### 2. Cron表达式

定时任务和CRON表达式在开发中使用也非常广泛；在学习时，总体上理解，对常用的知悉，开发时可以快速查询使用即可。

*相关文章*

- CRON表达式 - CRON表达式介绍和使用- https://pdai.tech/md/develop/cron/dev-cron-x-usage.html
  - 定时任务和CRON表达式在开发中使用也非常广泛，本文整理了CRON表达式和常见使用例子
- CRON表达式 - CRON生成和校验工具- https://pdai.tech/md/develop/cron/dev-cron-x-tools.html
  - 本文主要总结常用的在线CRON生成和校验工具，从而高效的写出正确的表达式

Cron表达式是一个字符串，字符串以5或6个空格隔开，分为6或7个域，每一个域代表一个含义，Cron有如下两种语法格式：

（1） Seconds Minutes Hours DayofMonth Month DayofWeek Year
（2）Seconds Minutes Hours DayofMonth Month DayofWeek

![属性值讲解](https://i-blog.csdnimg.cn/blog_migrate/6de71feb1e06f1ea447ab50efc57ac21.png)

（1）*：表示匹配该域的任意值。假如在Minutes域使用*, 即表示每分钟都会触发事件。

（2）?：只能用在DayofMonth和DayofWeek两个域。它也匹配域的任意值，但实际不会。因为DayofMonth和DayofWeek会相互影响。例如想在每月的20日触发调度，不管20日到底是星期几，则只能使用如下写法： 13 13 15 20 * ?, 其中最后一位只能用？，而不能使用*，如果使用*表示不管星期几都会触发，实际上并不是这样。

（3）-：表示范围。例如在Minutes域使用5-20，表示从5分到20分钟每分钟触发一次

（4）/：表示起始时间开始触发，然后每隔固定时间触发一次。例如在Minutes域使用5/20,则意味着5分钟触发一次，而25，45等分别触发一次.

（5）,：表示列出枚举值。例如：在Minutes域使用5,20，则意味着在5和20分每分钟触发一次。

（6）L：表示最后，只能出现在DayofWeek和DayofMonth域。如果在DayofWeek域使用5L,意味着在最后的一个星期四触发。

（7）W:表示有效工作日(周一到周五),只能出现在DayofMonth域，系统将在离指定日期的最近的有效工作日触发事件。例如：在 DayofMonth使用5W，如果5日是星期六，则将在最近的工作日：星期五，即4日触发。如果5日是星期天，则在6日(周一)触发；如果5日在星期一到星期五中的一天，则就在5日触发。另外一点，W的最近寻找不会跨过月份 。

（8）LW:这两个字符可以连用，表示在某个月最后一个工作日，即最后一个星期五。

（9）#:用于确定每个月第几个星期几，只能出现在DayofMonth域。例如在4#2，表示某月的第二个星期三。

Cron表达式范例：
每隔5秒执行一次：*/5 * * * * ?
每隔1分钟执行一次：0 */1 * * * ?
每天23点执行一次：0 0 23 * * ?
每天凌晨1点执行一次：0 0 1 * * ?
每月1号凌晨1点执行一次：0 0 1 1 * ?
每月最后一天23点执行一次：0 0 23 L * ?
每周星期天凌晨1点实行一次：0 0 1 ? * L

### 3. 线程

spring scheduled默认是所有定时任务都在一个线程中执行。也就是说定时任务1一直在执行，定时任务2一直在等待定时任务1执行完成。为了避免相互等待的情况，可以为定时任务配置线程池

```java
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(50));
    }
}
```

在程序启动后，会逐步启动50个线程，放在线程池中。每个定时任务会占用1个线程。但是相同的定时任务，执行的时候，还是在同一个线程中。

也可配置异步执行，相同的任务也不会相互影响。

添加配置：

```java
@Configuration
@EnableAsync
public class ScheduleConfig {
 
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(50);
        return taskScheduler;
    }
}
```

在方法上添加注解@Async

```java
public class TaskFileScheduleService {
 
 
    @Async
    @Scheduled(cron="0 */1 * * * ?")
    public void task1(){
  
    }
    
    @Async
    @Scheduled(cron="0 */1 * * * ?")
    public void task2(){
    
    }
}
```

这种方法，每次定时任务启动的时候，都会创建一个单独的线程来处理。也就是说同一个定时任务也会启动多个线程处理。



# 技术派定时任务

- <https://www.yuque.com/itwanger/az7yww/ap15lmvgser6ymag>





# 博客

- https://pdai.tech/md/spring/springboot/springboot-x-task-spring-task-timer.html
- 

# 参考文献

- <https://www.cnblogs.com/xfeiyun/p/17610953.html>
- <https://blog.csdn.net/reinsunshine/article/details/126094351>