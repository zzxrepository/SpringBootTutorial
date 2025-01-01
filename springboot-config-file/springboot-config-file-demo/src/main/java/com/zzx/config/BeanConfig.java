package com.zzx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public Person person() {
        Person person = new Person();
        person.setName("John");
        person.setAge(22);
        return person;
    }
}
