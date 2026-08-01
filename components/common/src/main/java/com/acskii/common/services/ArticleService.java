package com.acskii.common.services;

import com.acskii.common.exceptions.ArticleNotFoundException;
import com.acskii.common.models.Article;
import com.acskii.common.models.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.acskii.common.repos.ArticleRepository;

import java.time.Instant;

@Service
public class ArticleService {
    private final Logger log = LoggerFactory.getLogger(ArticleService.class);
    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /* Create */
    public Article create(Instant publish, Source source, String title, String author, String description, String url, String content) {
        Article article = new Article();
        article.setTitle(title);
        article.setAuthor(author == null ? "Unknown" : author);
        article.setDescription(description == null ? "N/A" : description);
        article.setContent(content == null ? "N/A" : content);
        article.setUrl(url == null ? "N/A" : url);
        article.setPublishedAt(publish);
        article.setSrc(source);

        Article saved = articleRepository.save(article);
        log.info("(create) article of ID [{}] with title [{}] was created", saved.getId(), title);
        return saved;
    }

    /* Read */
    public Article get(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));
    }

    /* Delete */
    public void delete(Long id) {
        Article article = get(id);
        articleRepository.delete(article);
        log.info("(delete) article [{} : \"{}\"] was deleted", id, article.getTitle());
    }
}
