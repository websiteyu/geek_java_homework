package com.mine.homework05.config;

import com.mine.homework05.bean.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public User user(){
        return new User(1,"zangs");
    }
}
