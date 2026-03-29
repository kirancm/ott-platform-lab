package com.opcon.bff.service;

import com.opcon.bff.client.SearchAggregatorClient;
import com.opcon.bff.dto.SearchResponse;
import com.opcon.bff.exception.DownstreamServiceException;
import com.opcon.bff.mapper.SearchMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchAggregatorClient searchAggregatorClient;
    private final SearchMapper searchMapper;

    public Mono<SearchResponse> search(String query) {
        return searchAggregatorClient.search(query)
                .map(payload -> searchMapper.toResponse(query, payload, false))
                .onErrorResume(DownstreamServiceException.class, error -> {
                    log.warn("Returning fallback search response for query={}", query, error);
                    return Mono.just(searchMapper.fallback(query));
                });
    }
}
