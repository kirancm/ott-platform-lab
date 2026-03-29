package com.opcon.bff.controller;

import com.opcon.bff.dto.ContentResponse;
import com.opcon.bff.service.ContentFacadeService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Validated
@RestController
@RequestMapping("/opcon/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentFacadeService contentFacadeService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public Mono<ContentResponse> getContent(@PathVariable("id") @NotBlank String contentId) {
        return contentFacadeService.getContent(contentId);
    }
}
