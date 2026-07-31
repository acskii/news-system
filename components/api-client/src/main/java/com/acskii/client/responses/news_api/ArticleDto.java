package com.acskii.client.responses.news_api;

import java.time.Instant;

public record ArticleDto(
        SourceDto source,
        String author,
        String title,
        String description,
        String url,
        String content,
        Instant publishedAt
) {}