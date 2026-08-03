package com.acskii.collector;

import com.acskii.client.ClientConfig;
import com.acskii.collector.services.NewsApiService;
import com.acskii.common.CommonConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
@Import({CommonConfig.class, ClientConfig.class})
public class CollectorApplication {
    static void main(String[] args) {
        SpringApplication.run(CollectorApplication.class, args);
    }
}
