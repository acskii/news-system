package com.acskii.common.repos;

import com.acskii.common.models.Article;
import com.acskii.common.models.Source;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByPublishedAtBetween(Instant start, Instant end);
}
