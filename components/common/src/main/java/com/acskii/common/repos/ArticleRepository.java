package com.acskii.common.repos;

import com.acskii.common.models.Article;
import com.acskii.common.models.Source;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findAllByOrderByPublishedAtDesc(Pageable pageable);

    Page<Article> findBySrcOrderByPublishedAtDesc(Source src, Pageable pageable);

    Page<Article> findByPublishedAtBetweenOrderByPublishedAtDesc(
            Instant start, Instant end, Pageable pageable);

    Page<Article> findBySrcAndPublishedAtBetweenOrderByPublishedAtDesc(
            Source src, Instant start, Instant end, Pageable pageable);
}
