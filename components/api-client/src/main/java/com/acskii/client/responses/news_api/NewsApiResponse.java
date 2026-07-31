package com.acskii.client.responses.news_api;

import java.util.List;

public record NewsApiResponse(
        String status,
        int totalResults,
        List<ArticleDto> articles
) {}