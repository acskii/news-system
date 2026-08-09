package com.acskii.collector.configs;

import com.acskii.client.clients.InternalClient;
import com.acskii.collector.services.NewsApiService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("dev")
public class DevConfig {
    @Bean
    public CommandLineRunner runOnStartup(NewsApiService newsService, InternalClient internalClient) {
        return args -> {
            newsService.populateSources();
            newsService.fetchTopHeadlines(List.of(new String[] {"us", "eg"}));
            internalClient.analyse();
            System.exit(0);
        };
    }
}
