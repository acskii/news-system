package com.acskii.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CollectorApplication {
    static void main(String[] args) {
        SpringApplication.run(CollectorApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner runOnStartup(NewsApiService newsService, NewsReport report) {
//        return args -> {
//            report.display(newsService.fetchTopHeadlines());
//            log.info("Completed!");
//            System.exit(0);
//        };
//    }
}
