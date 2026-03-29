package com.opcon.bff.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.opcon.bff.dto.SearchResponse;
import com.opcon.bff.dto.SearchResultResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SearchMapper {

    public SearchResponse toResponse(String query, JsonNode payload, boolean degraded) {
        return SearchResponse.builder()
                .query(query)
                .results(extractResults(payload))
                .degraded(degraded)
                .build();
    }

    public SearchResponse fallback(String query) {
        return SearchResponse.builder()
                .query(query)
                .results(List.of())
                .degraded(true)
                .build();
    }

    private List<SearchResultResponse> extractResults(JsonNode payload) {
        JsonNode itemsNode = payload;
        if (payload != null && payload.isObject()) {
            if (payload.has("results")) {
                itemsNode = payload.get("results");
            } else if (payload.has("items")) {
                itemsNode = payload.get("items");
            } else if (payload.has("content")) {
                itemsNode = payload.get("content");
            }
        }

        if (itemsNode == null || !itemsNode.isArray()) {
            return List.of();
        }

        List<SearchResultResponse> results = new ArrayList<>();
        for (JsonNode item : itemsNode) {
            results.add(SearchResultResponse.builder()
                    .id(text(item, "id"))
                    .title(text(item, "title"))
                    .type(firstNonBlank(item, "type", "entityType", "category"))
                    .description(firstNonBlank(item, "description", "summary", "overview"))
                    .status(firstNonBlank(item, "status", "workflowStatus", "state"))
                    .build());
        }
        return results;
    }

    private String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }

    private String firstNonBlank(JsonNode node, String... fields) {
        for (String field : fields) {
            String value = text(node, field);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
