package com.acskii.client;

import org.springframework.web.client.RestClient;

/*
    Abstract responsible for handling external API requests to third party APIs
    Return body as generic T
*/

public abstract class Client<T> {
    protected final RestClient client;

    public Client(String baseUrl) {
        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public abstract T get(String path);
}
