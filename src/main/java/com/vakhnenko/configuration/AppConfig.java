package com.vakhnenko.configuration;

import com.vakhnenko.entity.User;
import com.vakhnenko.utils.PreviousPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;

@Configuration
public class AppConfig {
    @Bean
    @SessionScope
    public User sessionUser() {
        return new User();
    }

    @Bean
    @SessionScope
    public PreviousPage sessionPreviousPage(){
        return new PreviousPage();
    }
}
