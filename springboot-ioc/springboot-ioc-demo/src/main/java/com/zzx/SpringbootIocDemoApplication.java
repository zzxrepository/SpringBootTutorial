package com.zzx;

import com.zzx.ioc.LifecycleDemoBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.zzx", "com.zzx.ioc"}) // 显式扫描
public class SpringbootIocDemoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SpringbootIocDemoApplication.class, args);

//        BeanPostProcessor beanPostProcessor = new BeanPostProcessor();
//        // 获取 Bean 并验证初始化逻辑
//        LifecycleDemoBean demoBean = context.getBean(LifecycleDemoBean.class);
//        System.out.println("Bean 初始化完成：" + demoBean.toString());
//
//        // 关闭容器以触发销毁逻辑
//        context.close();
    }
}