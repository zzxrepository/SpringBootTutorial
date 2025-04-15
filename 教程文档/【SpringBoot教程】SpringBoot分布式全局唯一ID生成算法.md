

![QQ_1742476653681](./assets/QQ_1742476653681.png)



## 一 背景描述

### 1.1 问题产生

在分布式系统中，怎么使用全局唯一id？

在分布式是，微服务的架构中，或者大数据[分库分表](https://so.csdn.net/so/search?q=分库分表&spm=1001.2101.3001.7020)中，多个不同节点怎么保持每台机器生成的主键id不重复，具有唯一性？

1. 方案1：mysql的自增主键； 设定一定的步长；如3台机器，3台节点初始值1,2,3，步长为3；机器A：1，4,7,10；机器B：2,5,8,11； 机器c：3,6,9,12
2. 方案2：使用uuid，无序且生成的串比较长，与mysql官方建议尽量使用较短的字符串冲突
3. 使用redis的原子性性产生主键，但是使用过程前期比较麻烦，需要搭建配置一堆东西。

这时，雪花算法是其中一个用于解决分布式 id 的高效方案，也是许多互联网公司在推荐使用的。

## 二 [雪花算法](https://so.csdn.net/so/search?q=雪花算法&spm=1001.2101.3001.7020)

### 2.1 雪花算法

**雪花算法：解决分布式高并发集群中，提供产生全局唯一的id，\**\*\*就是生成一个的 64 位比特位的 long 类型的唯一 id\*\**\**\**\*。\*\**\***

### ***\**\*2.2 雪花算法的结构\*\**\***

![img](https://i-blog.csdnimg.cn/blog_migrate/684b5af482e8c267fa2bec52e71fdd16.png)

最高 1 位固定值 0，因为生成的 id 是正整数，如果是 1 就是负数了。

接下来 41 位存储毫秒级时间戳，2^41/(1000*60*60*24*365)=69，大概可以使用 69 年。

再接下 10 位存储机器码，包括 5 位 datacenterId 和 5 位 workerId。最多可以部署 2^10=1024 台机器。

最后 12 位存储序列号。同一毫秒时间戳时，通过这个递增的序列号来区分。即对于同一台机器而言，同一毫秒时间戳下，可以生成 2^12=4096 个不重复 id。

###  ***\**\*2.3 雪花算法的使用\*\**\***

可以将雪花算法作为一个单独的服务进行部署，然后需要全局唯一 id 的系统，请求雪花算法服务获取 id 即可。例如机房号+机器号，机器号+服务号，或者是其他可区别标识的 10 位比特位的整数值都行。

### 2.4 案例

### ![img](https://i-blog.csdnimg.cn/blog_migrate/16b61841a5b2af93c348fa054c960dc3.png)

### 2.5 工具类

```java

```

###  2.6 优缺点

优点：

高并发分布式环境下生成不重复 id，每秒可生成百万个不重复 id。

基于时间戳，以及同一时间戳下序列号自增，基本保证 id 有序递增。

一般分布式ID只要求趋势递增，并不会严格要求递增，90%的需求都只要求趋势递增)

缺点：

服务器时钟回拨时可能会生成重复 id，解决办法：

百度开源的分布式唯一ID生成器UidGenerator

Leaf-- 美团点评分布式ID生成系统







# 用雪花ID和UUID做 MySQL主键，被领导怼了！



## **前言**

在mysql中设计表的时候,mysql官方推荐不要使用uuid或者不连续不重复的雪花id(long形且唯一),而是推荐连续自增的主键id,官方的推荐是auto_increment,那么为什么不建议采用uuid,使用uuid究竟有什么坏处？本篇博客我们就来分析这个问题,探讨一下内部的原因。

## **一、mysql和程序实例**

### **1.1. 要说明这个问题,我们首先来建立三张表**

分别是user_auto_key,user_uuid,user_random_key,分别表示自动增长的主键,uuid作为主键,随机key作为主键,其它我们完全保持不变.根据控制变量法,我们只把每个表的主键使用不同的策略生成,而其他的字段完全一样，然后测试一下表的插入速度和查询速度：

**注：这里的随机key其实是指用雪花算法算出来的前后不连续不重复**无规律**的id:一串18位长度的long值**

id自动生成表：

![图片](https://mmbiz.qpic.cn/mmbiz_png/R3InYSAIZkHmSa3uYDtUsnEOF15QHj3pWY1GsXVhZSFOTzkq26hTJib1ib2DRwCx1sIsSNaJWicp4JAIJNxmHq90A/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1&tp=wxpic)

用户uuid表

![图片](https://mmbiz.qpic.cn/mmbiz_png/R3InYSAIZkHmSa3uYDtUsnEOF15QHj3pyetBQncuA75EJpGibBu5pULvGHIyBqA6r0b4jYoyWvu0lUOia3H06Pxg/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1&tp=wxpic)

随机主键表：

![图片](https://mmbiz.qpic.cn/mmbiz_png/R3InYSAIZkHmSa3uYDtUsnEOF15QHj3p9RbDONp5zrFibPzwDAru7nEe0gXMxtP4TUSBGJjDjoJmBtucsJE3byA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1&tp=wxpic)

### **1.2. 光有理论不行,直接上程序,使用spring的jdbcTemplate来实现增查测试：**

技术框架：springboot+jdbcTemplate+junit+hutool,程序的原理就是连接自己的测试数据库,然后在相同的环境下写入同等数量的数据，来分析一下insert插入的时间来进行综合其效率，为了做到最真实的效果,所有的数据采用随机生成，比如名字、邮箱、地址都是随机生成，程序已上传自gitee,地址在文底。

```java
@SpringBootTest
classMysqlDemoApplicationTests{

    @Autowired
    private JdbcTemplateService jdbcTemplateService;
    @Autowired
    private AutoKeyTableService autoKeyTableService;
    @Autowired
    private UUIDKeyTableService uuidKeyTableService;
    @Autowired
    private RandomKeyTableService randomKeyTableService;

    @Test
    voidtestDBTime(){
        StopWatch stopwatch = new StopWatch("执行sql时间消耗");

        /**
         * auto_increment key任务
         */
        final String insertSql = "INSERT INTO user_key_auto(user_id,user_name,sex,address,city,email,state) VALUES(?,?,?,?,?,?,?)";

        List<UserKeyAuto> insertData = autoKeyTableService.getInsertData();
        stopwatch.start("自动生成key表任务开始");
        long start1 = System.currentTimeMillis();
        if (CollectionUtil.isNotEmpty(insertData)) {
            boolean insertResult = jdbcTemplateService.insert(insertSql, insertData, false);
            System.out.println(insertResult);
        }
        long end1 = System.currentTimeMillis();
        System.out.println("auto key消耗的时间:" + (end1 - start1));

        stopwatch.stop();


        /**
         * uudID的key
         */
        final String insertSql2 = "INSERT INTO user_uuid(id,user_id,user_name,sex,address,city,email,state) VALUES(?,?,?,?,?,?,?,?)";

        List<UserKeyUUID> insertData2 = uuidKeyTableService.getInsertData();
        stopwatch.start("UUID的key表任务开始");
        long begin = System.currentTimeMillis();
        if (CollectionUtil.isNotEmpty(insertData)) {
            boolean insertResult = jdbcTemplateService.insert(insertSql2, insertData2, true);
            System.out.println(insertResult);
        }
        long over = System.currentTimeMillis();
        System.out.println("UUID key消耗的时间:" + (over - begin));

        stopwatch.stop();


        /**
         * 随机的long值key
         */
        final String insertSql3 = "INSERT INTO user_random_key(id,user_id,user_name,sex,address,city,email,state) VALUES(?,?,?,?,?,?,?,?)";
        List<UserKeyRandom> insertData3 = randomKeyTableService.getInsertData();
        stopwatch.start("随机的long值key表任务开始");
        Long start = System.currentTimeMillis();
        if (CollectionUtil.isNotEmpty(insertData)) {
            boolean insertResult = jdbcTemplateService.insert(insertSql3, insertData3, true);
            System.out.println(insertResult);
        }
        Long end = System.currentTimeMillis();
        System.out.println("随机key任务消耗时间:" + (end - start));
        stopwatch.stop();


        String result = stopwatch.prettyPrint();
        System.out.println(result);
    }
```

### 1.3. 程序写入结果

user_key_auto写入结果：

![图片](https://mmbiz.qpic.cn/mmbiz_png/R3InYSAIZkHmSa3uYDtUsnEOF15QHj3pzayGFSG616MwxYV4jIKTgdVPrKQVn8iaF6l3ITibIuqChicAa2IGHs8cQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1&tp=wxpic)

user_random_key写入结果：

![图片](https://mmbiz.qpic.cn/mmbiz_png/R3InYSAIZkHmSa3uYDtUsnEOF15QHj3pWWia8xSgAlWZqBh0O1ia6ThkAYUV5xuUjhXw9rCVpEQYceictic3FNDHog/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1&tp=wxpic)

user_uuid表写入结果：

![图片](https://mmbiz.qpic.cn/mmbiz_png/R3InYSAIZkHmSa3uYDtUsnEOF15QHj3pvy57ROU8FxS4ibsrJ0VfbibAAuuaI8odibqSTQiawmzN6JguKicTazRqueA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1&tp=wxpic)

### **1.4. 效率测试结果**

![图片](https://mmbiz.qpic.cn/mmbiz_png/R3InYSAIZkHmSa3uYDtUsnEOF15QHj3pBqa9ZRsu7Fvk2OYXnlpqGQd9kapMTR3ZL1RMePsRjrMoDvqGXkEibYQ/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1&tp=wxpic)

在已有数据量为130W的时候：我们再来测试一下插入10w数据，看看会有什么结果：

![图片](https://mmbiz.qpic.cn/mmbiz_png/R3InYSAIZkHmSa3uYDtUsnEOF15QHj3pKbxn9XXe79V6MmZ37MapnEXYYyicLD0wvxoa0IcwcHiaiaDhsok5ypKKA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1&tp=wxpic)

可以看出在数据量100W左右的时候,uuid的插入效率垫底，并且在后序增加了130W的数据，uudi的时间又直线下降。时间占用量总体可以打出的效率排名为：auto_key>random_key>uuid,uuid的效率最低，在数据量较大的情况下，效率直线下滑。那么为什么会出现这样的现象呢？带着疑问,我们来探讨一下这个问题：

## **二、使用uuid和自增id的索引结构对比**

### **2.1. 使用自增id的内部结构**

![图片](https://mmbiz.qpic.cn/mmbiz_png/R3InYSAIZkHmSa3uYDtUsnEOF15QHj3pIibQ7e9k26QojYpoM6PyLW6MgFibDtyVzWUUEkCvjrnYzw1iaNahIN80Q/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1&tp=wxpic)

自增的主键的值是顺序的,所以Innodb把每一条记录都存储在一条记录的后面。当达到页面的最大填充因子时候(innodb默认的最大填充因子是页大小的15/16,会留出1/16的空间留作以后的  修改)：

①下一条记录就会写入新的页中，一旦数据按照这种顺序的方式加载，主键页就会近乎于顺序的记录填满，提升了页面的最大填充率，不会有页的浪费

②新插入的行一定会在原有的最大数据行下一行,mysql定位和寻址很快，不会为计算新行的位置而做出额外的消耗

③减少了页分裂和碎片的产生

### **2.2. 使用uuid的索引内部结构**

![图片](https://mmbiz.qpic.cn/mmbiz_png/R3InYSAIZkHmSa3uYDtUsnEOF15QHj3pzgibnVQGSvz6gVvmlsQvbcKY6SEPYbiatHUib2dew1WR7ZcVl6aicCUjuA/640?wx_fmt=png&wxfrom=5&wx_lazy=1&wx_co=1&tp=wxpic)

因为uuid相对顺序的自增id来说是毫无规律可言的,新行的值不一定要比之前的主键的值要大,所以innodb无法做到总是把新行插入到索引的最后,而是需要为新行寻找新的合适的位置从而来分配新的空间。这个过程需要做很多额外的操作，数据的毫无顺序会导致数据分布散乱，将会导致以下的问题：

①：写入的目标页很可能已经刷新到磁盘上并且从缓存上移除，或者还没有被加载到缓存中，innodb在插入之前不得不先找到并从磁盘读取目标页到内存中，这将导致大量的随机IO

**②**：因为写入是乱序的,innodb不得不频繁的做页分裂操作,以便为新的行分配空间,页分裂导致移动大量的数据，一次插入最少需要修改三个页以上

**③**：由于频繁的页分裂，页会变得稀疏并被不规则的填充，最终会导致数据会有碎片

在把随机值（uuid和雪花id）载入到聚簇索引(innodb默认的索引类型)以后,有时候会需要做一次OPTIMEIZE TABLE来重建表并优化页的填充，这将又需要一定的时间消耗。

结论：使用innodb应该尽可能的按主键的自增顺序插入，并且尽可能使用单调的增加的聚簇键的值来插入新行

### **2.3. 使用自增id的缺点**

那么使用自增的id就完全没有坏处了吗？并不是，自增id也会存在以下几点问题：

①：别人一旦爬取你的数据库,就可以根据数据库的自增id获取到你的业务增长信息，很容易分析出你的经营情况

②：对于高并发的负载，innodb在按主键进行插入的时候会造成明显的锁争用，主键的上界会成为争抢的热点，因为所有的插入都发生在这里，并发插入会导致间隙锁竞争

③：Auto_Increment锁机制会造成自增锁的抢夺,有一定的性能损失

附：Auto_increment的锁争抢问题，如果要改善需要调优innodb_autoinc_lock_mode的配置









# 参考文献

- <https://blog.csdn.net/u011066470/article/details/128769241>
- <https://blog.csdn.net/weixin_44929998/article/details/129799790>
- <https://blog.csdn.net/qq_25046827/article/details/125699393>
- <https://blog.csdn.net/best_luxi/article/details/120245974>
- 百度的方案：https://cloud.tencent.com/developer/article/1680001
- 9种分布式ID：https://mp.weixin.qq.com/s?__biz=MzU0OTE4MzYzMw%3D%3D&chksm=fbb29e35ccc51723d20211820715346c70c25e6eaf49d80cde0314aff4ab2d072658038f1a3b&idx=3&mid=2247489483&scene=21&sn=fbf4945e6474d19ab98e99d4088fe441#wechat_redirect