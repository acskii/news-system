package com.acskii.collector.configs;

import com.acskii.client.clients.InternalClient;
import com.acskii.collector.services.NewsApiService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@Profile("prod")
@EnableScheduling
public class ProdConfig {
    private final NewsApiService newsService;
    private final InternalClient internalClient;

    public ProdConfig(NewsApiService newsService, InternalClient internalClient) {
        this.newsService = newsService;
        this.internalClient = internalClient;
    }

    @Scheduled(cron = "0 0 8 * * *") // Every day at 08:00
    public void scheduledNewsCollection() {
        newsService.populateSources();
        newsService.fetchTopHeadlines(List.of(new String[] {"us", "eg"}));
        internalClient.analyse();
    }
}
