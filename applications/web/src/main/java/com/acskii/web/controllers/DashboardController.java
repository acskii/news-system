package com.acskii.web.controllers;

import com.acskii.common.models.Article;
import com.acskii.common.models.Source;
import com.acskii.common.repos.ArticleRepository;
import com.acskii.common.repos.SourceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Controller
public class DashboardController {

    private final ArticleRepository articleRepository;
    private final SourceRepository sourceRepository;

    private static final int PAGE_SIZE = 12; // 3 columns × 4 rows

    public DashboardController(ArticleRepository articleRepository,
                               SourceRepository sourceRepository) {
        this.articleRepository = articleRepository;
        this.sourceRepository = sourceRepository;
    }

    @GetMapping("/")
    public String dashboard(
            @RequestParam(required = false) Integer sourceId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        // Load all sources for the dropdown
        List<Source> allSources = sourceRepository.findAll();
        model.addAttribute("sources", allSources);

        // Build pageable (newest first)
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("publishedAt").descending());

        // Convert dates
        Instant start = (startDate != null) ? startDate.atStartOfDay(ZoneOffset.UTC).toInstant() : null;
        Instant end = (endDate != null) ? endDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant() : null;

        Page<Article> articlePage;

        if (sourceId != null && sourceId > 0) {
            Optional<Source> sourceOpt = sourceRepository.findById(sourceId);
            if (sourceOpt.isPresent()) {
                Source source = sourceOpt.get();
                if (start != null && end != null) {
                    articlePage = articleRepository
                            .findBySrcAndPublishedAtBetweenOrderByPublishedAtDesc(source, start, end, pageable);
                } else if (start != null) {
                    articlePage = articleRepository
                            .findBySrcAndPublishedAtBetweenOrderByPublishedAtDesc(source, start, Instant.now(), pageable);
                } else if (end != null) {
                    articlePage = articleRepository
                            .findBySrcAndPublishedAtBetweenOrderByPublishedAtDesc(source, Instant.EPOCH, end, pageable);
                } else {
                    articlePage = articleRepository
                            .findBySrcOrderByPublishedAtDesc(source, pageable);
                }
            } else {
                articlePage = articleRepository.findAllByOrderByPublishedAtDesc(pageable);
            }
        } else {
            // No source filter
            if (start != null && end != null) {
                articlePage = articleRepository
                        .findByPublishedAtBetweenOrderByPublishedAtDesc(start, end, pageable);
            } else if (start != null) {
                articlePage = articleRepository
                        .findByPublishedAtBetweenOrderByPublishedAtDesc(start, Instant.now(), pageable);
            } else if (end != null) {
                articlePage = articleRepository
                        .findByPublishedAtBetweenOrderByPublishedAtDesc(Instant.EPOCH, end, pageable);
            } else {
                articlePage = articleRepository.findAllByOrderByPublishedAtDesc(pageable);
            }
        }

        model.addAttribute("articles", articlePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlePage.getTotalPages());
        model.addAttribute("totalItems", articlePage.getTotalElements());
        model.addAttribute("selectedSourceId", sourceId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("hasNext", articlePage.hasNext());
        model.addAttribute("hasPrevious", articlePage.hasPrevious());

        return "dashboard";
    }

    @GetMapping("/article/{id}")
    public String articleDetail(@PathVariable Long id, Model model) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        model.addAttribute("article", article);
        return "article";
    }
}