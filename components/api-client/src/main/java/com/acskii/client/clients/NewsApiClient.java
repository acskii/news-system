package com.acskii.client.clients;

import com.acskii.client.Client;
import com.acskii.client.misc.news_api.Category;
import com.acskii.client.responses.news_api.NewsApiResponse;
import com.acskii.client.responses.news_api.NewsApiSourceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NewsApiClient extends Client<NewsApiResponse> {
    private final String apiKey;

    /*
        According to Top Headlines endpoint (31/7/2026):
        https://newsapi.org/docs/endpoints/top-headlines
    */

    // The 2-letter ISO 3166-1 code of the country you want to get headlines for.
    private String country = "us";

    // The category you want to get headlines for.
    private Category category;

    // Keywords or a phrase to search for.
    private String q;

    // The number of results to return per page (request). 20 is the default, 100 is the maximum.
    private int pageSize = 20;

    // Use this to page through the results if the total results found is greater than the page size.
    private int page = 0;

    public NewsApiClient(
            @Value("${sources.news_api.base_url}") String baseUrl,
            @Value("${sources.news_api.key}") String apiKey
    ) {
        super(baseUrl);
        this.apiKey = apiKey;
    }

    @Override
    public NewsApiResponse get(String path) {
        return this.client.get().uri((builder) -> {
                    builder.path(path)
                            .queryParam("apiKey", this.apiKey)
                            .queryParam("country", this.country)
                            .queryParam("page", this.page)
                            .queryParam("pageSize", this.pageSize);
                    if (this.category != null) builder.queryParam("category", this.category.getValue());
                    if (this.q != null) builder.queryParam("q", this.q);
                    return builder.build();
                })
                .retrieve()
                .body(NewsApiResponse.class);
    }

    public NewsApiSourceResponse source() {
        return this.client.get().uri((builder) -> {
                    builder.path("/top-headlines/sources")
                            .queryParam("apiKey", this.apiKey);
                    return builder.build();
                })
                .retrieve()
                .body(NewsApiSourceResponse.class);
    }

    /* Getters & Setters */
    public void setCountry(String country) {this.country = country;}
    public Category getCategory() {return this.category;}
    public void setCategory(Category category) {this.category = category;}
    public String getCountry() {return this.country;}
    public String getQ() {return this.q;}
    public void setQ(String q) {this.q = q;}
    public int getPageSize() {return this.pageSize;}
    public void setPageSize(int pageSize) {this.pageSize = pageSize;}
    public int getPage() {return this.page;}
    public void setPage(int page) {this.page = page;}
}
