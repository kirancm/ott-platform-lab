package com.opcon.bff.client;

import com.opcon.bff.config.OpConWebClientProperties;
import com.opcon.bff.dto.downstream.ContentServiceResponse;
import com.opcon.bff.exception.DownstreamServiceException;
import com.opcon.bff.exception.ResourceNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentClient {

    @Qualifier("contentWebClient")
    private final WebClient webClient;
    private final OpConWebClientProperties webClientProperties;

    @CircuitBreaker(name = "contentClient")
    public Mono<ContentServiceResponse> getContent(String contentId) {
        return webClient.get()
                .uri("/movies/{id}", contentId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.statusCode().value() == 404
                        ? Mono.error(new ResourceNotFoundException("Content not found: " + contentId))
                        : response.createException())
                .bodyToMono(ContentServiceResponse.class)
                .timeout(Duration.ofSeconds(webClientProperties.timeoutSeconds()))
                .retryWhen(Retry.backoff(webClientProperties.retryAttempts(), Duration.ofMillis(200))
                        .filter(error -> !(error instanceof ResourceNotFoundException)))
                .doOnError(error -> log.warn("Content downstream failed for contentId={}", contentId, error))
                .onErrorMap(error -> {
                    if (error instanceof RuntimeException runtimeException
                            && (runtimeException instanceof ResourceNotFoundException
                            || runtimeException instanceof DownstreamServiceException)) {
                        return runtimeException;
                    }
                    return new DownstreamServiceException("Content service lookup failed", error);
                });
    }
}
