package com.opcon.bff.client;

import com.opcon.bff.config.OpConWebClientProperties;
import com.opcon.bff.dto.downstream.CwmJobDto;
import com.opcon.bff.dto.downstream.CwmWorkflowDto;
import com.opcon.bff.dto.downstream.DownstreamPageResponse;
import com.opcon.bff.exception.DownstreamServiceException;
import com.opcon.bff.exception.ResourceNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
@Component
@RequiredArgsConstructor
public class CwmClient {

    private static final ParameterizedTypeReference<List<CwmJobDto>> JOB_LIST_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<DownstreamPageResponse<CwmWorkflowDto>> WORKFLOW_PAGE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    @Qualifier("cwmWebClient")
    private final WebClient webClient;
    private final OpConWebClientProperties webClientProperties;

    @CircuitBreaker(name = "cwmClient")
    public Mono<CwmWorkflowDto> getWorkflow(UUID workflowId) {
        return webClient.get()
                .uri("/workflows/{workflowId}", workflowId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.statusCode().value() == 404
                        ? Mono.error(new ResourceNotFoundException("Workflow not found: " + workflowId))
                        : response.createException())
                .bodyToMono(CwmWorkflowDto.class)
                .timeout(Duration.ofSeconds(webClientProperties.timeoutSeconds()))
                .retryWhen(retrySpec("getWorkflow"))
                .doOnError(error -> log.warn("Failed to fetch workflow {}", workflowId, error))
                .onErrorMap(error -> wrap("CWM workflow lookup failed", error));
    }

    @CircuitBreaker(name = "cwmClient")
    public Mono<List<CwmJobDto>> getWorkflowJobs(UUID workflowId) {
        return webClient.get()
                .uri("/workflows/{workflowId}/jobs", workflowId)
                .retrieve()
                .bodyToMono(JOB_LIST_TYPE)
                .timeout(Duration.ofSeconds(webClientProperties.timeoutSeconds()))
                .retryWhen(retrySpec("getWorkflowJobs"))
                .doOnError(error -> log.warn("Failed to fetch jobs for workflow {}", workflowId, error))
                .onErrorMap(error -> wrap("CWM workflow jobs lookup failed", error));
    }

    @CircuitBreaker(name = "cwmClient")
    public Mono<DownstreamPageResponse<CwmWorkflowDto>> getWorkflows(int page, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/workflows").queryParam("page", page).queryParam("size", size).build())
                .retrieve()
                .bodyToMono(WORKFLOW_PAGE_TYPE)
                .timeout(Duration.ofSeconds(webClientProperties.timeoutSeconds()))
                .retryWhen(retrySpec("getWorkflows"))
                .doOnError(error -> log.warn("Failed to fetch workflow page page={} size={}", page, size, error))
                .onErrorMap(error -> wrap("CWM workflow page lookup failed", error));
    }

    @CircuitBreaker(name = "cwmClient")
    public Mono<Void> retryJob(UUID jobId) {
        return webClient.post()
                .uri("/jobs/{jobId}/retry", jobId)
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(webClientProperties.timeoutSeconds()))
                .retryWhen(retrySpec("retryJob"))
                .doOnError(error -> log.warn("Failed to retry job {}", jobId, error))
                .onErrorMap(error -> wrap("CWM retry failed", error))
                .then();
    }

    private Retry retrySpec(String operation) {
        if (webClientProperties.retryAttempts() == 0) {
            return Retry.max(0);
        }

        return Retry.backoff(webClientProperties.retryAttempts(), Duration.ofMillis(200))
                .filter(this::isRetryable)
                .doBeforeRetry(signal -> log.warn("Retrying {} attempt {}", operation, signal.totalRetries() + 1));
    }

    private boolean isRetryable(Throwable throwable) {
        return !(throwable instanceof ResourceNotFoundException);
    }

    private RuntimeException wrap(String message, Throwable error) {
        if (error instanceof RuntimeException runtimeException
                && (runtimeException instanceof ResourceNotFoundException
                || runtimeException instanceof DownstreamServiceException)) {
            return runtimeException;
        }
        return new DownstreamServiceException(message, error);
    }
}
