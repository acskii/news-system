package com.acskii.client.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InternalClient {
    private final Logger log = LoggerFactory.getLogger(InternalClient.class);
    private final RestClient client;

    public InternalClient(
            @Value("${internal.analyser.base_url}") String baseUrl) {
        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public void analyse() {
        this.client.get().uri("/process")
                         .retrieve()
                         .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                            log.error("Client error occurred while calling Analyser Service: {}", response.getStatusCode());
                            throw new RuntimeException("Analyser service client error: " + response.getStatusCode());
                         })
                         .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                            log.error("Server error occurred on Analyser Service: {}", response.getStatusCode());
                            throw new RuntimeException("Analyser service failed internally: " + response.getStatusCode());
                         })
                         .toBodilessEntity();
        log.info("[ANALYSER] (process) Successfully triggered daily analytics process");
    }
}
