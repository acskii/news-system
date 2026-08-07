package com.acskii.common.services;

import com.acskii.common.exceptions.AnalyticNotFoundException;
import com.acskii.common.exceptions.ArticleNotFoundException;
import com.acskii.common.models.Analytic;
import com.acskii.common.models.Article;
import com.acskii.common.repos.AnalyticRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AnalyticService {
    private final Logger log = LoggerFactory.getLogger(AnalyticService.class);
    private final AnalyticRepository analyticRepository;

    public AnalyticService(AnalyticRepository analyticRepository) {
        this.analyticRepository = analyticRepository;
    }

    /* Create */
    public Analytic create(Instant analysedAt, int totalArticles, String trendingJson, String breakingJson) {
        Analytic analytic = new Analytic();
        analytic.setTotalArticles(totalArticles);
        analytic.setAnalysedAt(analysedAt);
        analytic.setBreakingNews(breakingJson);
        analytic.setTrendingKeywords(trendingJson);
        Analytic saved = analyticRepository.save(analytic);
        log.info("(create) analytic of ID [{}] for day [{}] was created", saved.getId(), analysedAt.toString());
        return saved;
    }

    /* Read */
    public Analytic get(Long id) {
        return analyticRepository.findById(id)
                .orElseThrow(() -> new AnalyticNotFoundException(id));
    }

    public List<Analytic> getAll() {
        return analyticRepository.findAll();
    }

    public Analytic getLatest() {
        return analyticRepository.findFirstByOrderByAnalysedAtDesc()
                .orElseThrow(() -> new AnalyticNotFoundException("No analytic record found in database"));
    }

    /* Delete */
    public void delete(Long id) {
        Analytic analytic = get(id);
        analyticRepository.delete(analytic);
        log.info("(delete) analytic [{} : {}] was deleted", id, analytic.getAnalysedAt());
    }
}