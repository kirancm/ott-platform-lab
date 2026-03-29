package com.opcon.bff.controller;

import com.opcon.bff.dto.SearchResponse;
import com.opcon.bff.service.SearchService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Validated
@RestController
@RequestMapping("/opcon/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public Mono<SearchResponse> search(@RequestParam("q") @NotBlank @Size(min = 2, max = 100) String query) {
        return searchService.search(query);
    }
}
