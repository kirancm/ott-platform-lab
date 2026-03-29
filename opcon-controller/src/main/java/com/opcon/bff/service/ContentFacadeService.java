package com.opcon.bff.service;

import com.opcon.bff.client.ContentClient;
import com.opcon.bff.dto.ContentResponse;
import com.opcon.bff.exception.DownstreamServiceException;
import com.opcon.bff.exception.ResourceNotFoundException;
import com.opcon.bff.mapper.ContentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentFacadeService {

    private final ContentClient contentClient;
    private final ContentMapper contentMapper;

    public Mono<ContentResponse> getContent(String contentId) {
        return contentClient.getContent(contentId)
                .map(response -> contentMapper.toResponse(response, false))
                .onErrorResume(ResourceNotFoundException.class, Mono::error)
                .onErrorResume(DownstreamServiceException.class, error -> {
                    log.warn("Returning fallback content response for contentId={}", contentId, error);
                    return Mono.just(contentMapper.fallback(contentId));
                });
    }
}
