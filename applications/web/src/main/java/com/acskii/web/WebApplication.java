package com.acskii.web;

import com.acskii.common.CommonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(CommonConfig.class)
public class WebApplication {
    static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
