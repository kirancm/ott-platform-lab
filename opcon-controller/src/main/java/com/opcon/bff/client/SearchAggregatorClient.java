package com.opcon.bff.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.opcon.bff.config.OpConWebClientProperties;
import com.opcon.bff.exception.DownstreamServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchAggregatorClient {

    @Qualifier("searchWebClient")
    private final WebClient webClient;
    private final OpConWebClientProperties webClientProperties;

    @CircuitBreaker(name = "searchClient")
    public Mono<JsonNode> search(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/search").queryParam("q", query).build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .timeout(Duration.ofSeconds(webClientProperties.timeoutSeconds()))
                .retryWhen(Retry.backoff(webClientProperties.retryAttempts(), Duration.ofMillis(200)))
                .doOnError(error -> log.warn("Search downstream failed for query={}", query, error))
                .onErrorMap(error -> new DownstreamServiceException("Search service lookup failed", error));
    }
}
