package com.acskii.client.responses.news_api;

import java.util.List;

public record NewsApiSourceResponse(
        String status,
        List<SourceDto> sources
) {}
