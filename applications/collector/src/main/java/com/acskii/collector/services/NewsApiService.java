package com.acskii.collector.services;

import com.acskii.client.clients.NewsApiClient;
import com.acskii.client.misc.news_api.Category;
import com.acskii.client.responses.news_api.ArticleDto;
import com.acskii.client.responses.news_api.NewsApiResponse;
import com.acskii.common.exceptions.SourceNotFoundException;
import com.acskii.common.models.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.acskii.common.services.ArticleService;
import com.acskii.common.services.SourceService;

import java.time.LocalDate;
import java.util.List;

@Service
public class NewsApiService {
    private final Logger log = LoggerFactory.getLogger(NewsApiService.class);
    private final NewsApiClient client;
    private final SourceService sourceService;
    private final ArticleService articleService;

    public NewsApiService(NewsApiClient client, SourceService sourceService, ArticleService articleService) {
        this.client = client;
        this.sourceService = sourceService;
        this.articleService = articleService;
    }

    /*
        Fetch Top Headlines
        https://newsapi.org/docs/endpoints/top-headlines
    */
    public void fetchTopHeadlines(List<String> countries) {
        List<Category> categories = List.of(Category.values());
        int count = 0;

        long startTime = System.nanoTime();
        for (String country : countries) {
            for (Category category : categories) {
                this.client.setCategory(category);
                this.client.setCountry(country);
                NewsApiResponse response = this.client.get("/top-headlines");

                if (response.status().equals("ok")) {
                    for (ArticleDto dto : response.articles()) {
                        if (dto.url() == null || dto.title() == null) continue;

                        Source source = getSource(dto);
                        articleService.create(
                                dto.publishedAt(),
                                source,
                                dto.title(),
                                dto.author(),
                                dto.description(),
                                dto.url(),
                                dto.content()
                        );
                        count++;
                    }

                } else {
                    log.warn("[News API] (fetch) (headlines) [{}][{}] Status: {}", country, category, response.status());
                }
            }
        }
        long endTime = System.nanoTime();
        log.info("[RESULT OF RUN] [{}]: Time for execution: {} s, Articles saved: {}",
                LocalDate.now(),
                (endTime - startTime) / 1000000000,
                count);
    }

    private Source getSource(ArticleDto dto) {
        try {
            return sourceService.getByName(dto.source().name());
        } catch (SourceNotFoundException e) {
            return sourceService.create(dto.source().name(), "N/A", null);
        }
        // TODO: need to have endpoint for all sources from this api alone
    }

}
