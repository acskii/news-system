package com.acskii.client;

import com.acskii.client.clients.NewsApiClient;
import com.acskii.client.misc.news_api.Category;
import com.acskii.client.responses.news_api.NewsApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsApiClientTest {

    private static final String BASE_URL = "https://newsapi.org/v2";
    private static final String API_KEY = "test-api-key";

    @Mock
    private RestClient mockRestClient;

    // Use raw types to avoid generic issues with the fluent API
    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private NewsApiClient client;
    private URI capturedUri;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() throws Exception {
        client = new NewsApiClient(BASE_URL, API_KEY);

        // Inject mocked RestClient into the protected field
        Field clientField = NewsApiClient.class.getSuperclass().getDeclaredField("client");
        clientField.setAccessible(true);
        clientField.set(client, mockRestClient);

        // Stub the fluent chain
        when(mockRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
            // Extract the lambda and apply it to a real UriComponentsBuilder
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL);
            capturedUri = uriFunction.apply(builder);
            return requestHeadersSpec;
        });
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NewsApiResponse.class)).thenReturn(new NewsApiResponse("ok", 0, null));
    }

    @Test
    void shouldUseDefaultParametersWhenNoneSet() {
        client.get("/top-headlines");

        assertThat(capturedUri).isNotNull();
        assertThat(capturedUri.getQuery()).contains("apiKey=" + API_KEY);
        assertThat(capturedUri.getQuery()).contains("country=us");
        assertThat(capturedUri.getQuery()).contains("page=0");
        assertThat(capturedUri.getQuery()).contains("pageSize=20");
        assertThat(capturedUri.getQuery()).doesNotContain("category=");
        assertThat(capturedUri.getQuery()).doesNotContain("q=");
    }

    @Test
    void shouldSetCountryParameter() {
        client.setCountry("gb");
        client.get("/top-headlines");
        assertThat(capturedUri.getQuery()).contains("country=gb");
    }

    @Test
    void shouldSetPageParameter() {
        client.setPage(2);
        client.get("/top-headlines");
        assertThat(capturedUri.getQuery()).contains("page=2");
    }

    @Test
    void shouldSetPageSizeParameter() {
        client.setPageSize(50);
        client.get("/top-headlines");
        assertThat(capturedUri.getQuery()).contains("pageSize=50");
    }

    @Test
    void shouldAddCategoryParameterWhenSet() {
        client.setCategory(Category.TECHNOLOGY);
        client.get("/top-headlines");
        assertThat(capturedUri.getQuery()).contains("category=technology");
    }

    @Test
    void shouldAddQParameterWhenSet() {
        client.setQ("apple");
        client.get("/top-headlines");
        assertThat(capturedUri.getQuery()).contains("q=apple");
    }

    @Test
    void shouldIncludeAllSetParameters() {
        client.setCountry("fr");
        client.setCategory(Category.SCIENCE);
        client.setQ("climate");
        client.setPageSize(100);
        client.setPage(3);

        client.get("/top-headlines");

        String query = capturedUri.getQuery();
        assertThat(query).contains("apiKey=" + API_KEY);
        assertThat(query).contains("country=fr");
        assertThat(query).contains("category=science");
        assertThat(query).contains("q=climate");
        assertThat(query).contains("pageSize=100");
        assertThat(query).contains("page=3");
    }

    @Test
    void shouldNotIncludeCategoryWhenNull() {
        client.setCategory(null);
        client.get("/top-headlines");
        assertThat(capturedUri.getQuery()).doesNotContain("category=");
    }

    @Test
    void shouldNotIncludeQWhenNull() {
        client.setQ(null);
        client.get("/top-headlines");
        assertThat(capturedUri.getQuery()).doesNotContain("q=");
    }

    @Test
    void shouldReturnResponseBody() {
        NewsApiResponse expectedResponse = new NewsApiResponse("ok", 42, null);
        when(responseSpec.body(NewsApiResponse.class)).thenReturn(expectedResponse);

        NewsApiResponse actual = client.get("/top-headlines");
        assertThat(actual).isSameAs(expectedResponse);
    }
}