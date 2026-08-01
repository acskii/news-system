package com.acskii.client.responses.news_api;

public record SourceDto(
        String id,
        String name,
        String description,
        String country
) {}