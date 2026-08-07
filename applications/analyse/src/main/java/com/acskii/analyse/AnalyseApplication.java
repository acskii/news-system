package com.acskii.analyse;

import com.acskii.analyse.services.AnalyticProcessor;
import com.acskii.common.CommonConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
@Import(CommonConfig.class)
public class AnalyseApplication {
    static void main(String[] args) {
        SpringApplication.run(AnalyseApplication.class, args);
    }

    @Bean
    public CommandLineRunner runOnStartup(AnalyticProcessor processor) {
        return args -> {
            processor.processDaily();
            System.exit(0);
        };
    }
}
