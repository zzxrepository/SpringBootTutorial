package com.zzx.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class PersonConfig {
    @Value("${person.name}")
    private String name;
}
