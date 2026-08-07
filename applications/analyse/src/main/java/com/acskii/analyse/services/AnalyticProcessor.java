package com.acskii.analyse.services;

import com.acskii.common.models.Analytic;
import com.acskii.common.models.Article;
import com.acskii.common.services.AnalyticService;
import com.acskii.common.services.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AnalyticProcessor {
    private final Logger log = LoggerFactory.getLogger(AnalyticProcessor.class);
    private final List<String> stopWords;

    private final ArticleService articleService;
    private final AnalyticService analyticService;
    private final ObjectMapper mapper;

    public AnalyticProcessor(ArticleService articleService, AnalyticService analyticService, ObjectMapper mapper, StopWordLoader loader) {
        this.articleService = articleService;
        this.analyticService = analyticService;
        this.mapper = mapper;
        this.stopWords = loader.getStopWords();
    }

    private Map<String, Long> extractKeywordFrequencies(List<Article> articles) {
        return articles.stream()
                .map(Article::getTitle)
                .filter(Objects::nonNull)
                .flatMap(title -> Arrays.stream(title.toLowerCase().split("\\W+")))
                .filter(word -> word.length() > 3 && !stopWords.contains(word))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    public Analytic processDaily() {
        Instant now = Instant.now();
        Instant twentyFourHoursAgo = now.minus(2, ChronoUnit.DAYS);
        Instant eightDaysAgo = now.minus(8, ChronoUnit.DAYS);

        List<Article> today = articleService.getByPublishedAt(twentyFourHoursAgo, now);
        List<Article> historical = articleService.getByPublishedAt(eightDaysAgo, twentyFourHoursAgo);

        List<Map<String, Object>> trending = computeSpikeTrends(today, historical);
        List<Map<String, Object>> breakingNews = detectBreakingNews(today);

        return analyticService.create(
                now,
                today.size(),
                mapper.writeValueAsString(Map.of(
                        "trending_topics", trending
                )),
                mapper.writeValueAsString(Map.of(
                        "breaking_news", breakingNews
                ))
        );
    }

    private List<Map<String, Object>> computeSpikeTrends(List<Article> today, List<Article> historical) {
        Map<String, Long> todayCounts = extractKeywordFrequencies(today);
        Map<String, Long> historicalCounts = extractKeywordFrequencies(historical);

        List<Map<String, Object>> results = new ArrayList<>();

        todayCounts.forEach((keyword, countToday) -> {
            long count7DayTotal = historicalCounts.getOrDefault(keyword, 0L);
            double dailyBaselineAvg = count7DayTotal / 7.0;
            double spikeScore = countToday / (dailyBaselineAvg + 1.0);

            if (countToday >= 2 && spikeScore >= 1.8) {
                results.add(Map.of(
                        "keyword", keyword,
                        "todayCount", countToday,
                        "spikeScore", Math.round(spikeScore * 100.0) / 100.0
                ));
            }
        });

        results.sort((a, b) -> Double.compare((Double) b.get("spikeScore"), (Double) a.get("spikeScore")));
        return results;
    }

    private List<Map<String, Object>> detectBreakingNews(List<Article> articles) {
        List<Article> sorted = articles.stream()
                .filter(a -> a.getPublishedAt() != null)
                .sorted(Comparator.comparing(Article::getPublishedAt))
                .toList();

        List<Map<String, Object>> clusters = new ArrayList<>();

        // Sliding 60-minute window
        for (int i = 0; i < sorted.size(); i++) {
            Article base = sorted.get(i);
            Instant windowEnd = base.getPublishedAt().plus(60, ChronoUnit.MINUTES);

            List<Article> windowArticles = new ArrayList<>();
            for (int j = i; j < sorted.size(); j++) {
                Article target = sorted.get(j);
                if (target.getPublishedAt().isBefore(windowEnd)) {
                    windowArticles.add(target);
                } else {
                    break;
                }
            }

            // Flag as Breaking News if >= 4 articles published within 60 mins
            if (windowArticles.size() >= 4) {
                Set<String> uniqueSources = windowArticles.stream()
                        .map(a -> a.getSrc() != null ? a.getSrc().getName() : "Unknown")
                        .collect(Collectors.toSet());

                if (uniqueSources.size() >= 2) { // Ensures cross-publisher validation
                    clusters.add(Map.of(
                            "timestamp", base.getPublishedAt().toString(),
                            "articleCount", windowArticles.size(),
                            "uniqueSourcesCount", uniqueSources.size(),
                            "sampleTitle", base.getTitle()
                    ));
                    i += windowArticles.size() - 1; // Skip ahead past this cluster
                }
            }
        }
        return clusters;
    }


}
