package com.me.microservices.rag_service.query.service;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder.Op;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.me.microservices.rag_service.ingest.exception.VectorStoreException;
import com.me.microservices.rag_service.query.dto.response.QueryChunk;
import com.me.microservices.rag_service.query.dto.response.QueryResponse;
import com.me.microservices.rag_service.query.exception.ForbiddenQueryException;
import com.me.microservices.rag_service.query.exception.InvalidQueryException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QueryService {

    private static final int DEFAULT_TOP_K = 4;
    private static final int MAX_TOP_K = 10;
    private static final int MAX_QUERY_LENGTH = 2000;

    private final VectorStore vectorStore;

    public QueryResponse query(String rawQuery, Integer rawTopK, Jwt jwt) {
        String area = jwt.getClaimAsString("area");
        String hierarchy = jwt.getClaimAsString("hierarchy");

        if (area == null) {
            throw new ForbiddenQueryException("Only employees can query indexed documents");
        }

        String query = rawQuery == null ? "" : rawQuery.strip();
        if (query.isEmpty()) {
            throw new InvalidQueryException("Query must not be blank");
        }
        if (query.length() > MAX_QUERY_LENGTH) {
            throw new InvalidQueryException("Query must be at most " + MAX_QUERY_LENGTH + " characters");
        }

        int topK = rawTopK == null ? DEFAULT_TOP_K : rawTopK;
        if (topK < 1 || topK > MAX_TOP_K) {
            throw new InvalidQueryException("topK must be between 1 and " + MAX_TOP_K);
        }

        SearchRequest request = SearchRequest.builder()
            .query(query)
            .topK(topK)
            .similarityThresholdAll()
            .filterExpression(buildFilter(area, hierarchy))
            .build();

        List<Document> hits;
        try {
            hits = vectorStore.similaritySearch(request);
        } catch (RuntimeException exception) {
            throw new VectorStoreException("Could not search vectors: " + exception.getMessage());
        }

        List<QueryChunk> results = hits.stream().map(this::toChunk).toList();
        return new QueryResponse(query, topK, results.size(), results);
    }

    private Filter.Expression buildFilter(String area, String hierarchy) {
        FilterExpressionBuilder builder = new FilterExpressionBuilder();

        Op publicDocs = builder.eq("confidentialityLevel", "PUBLIC");
        Op allAreasDocs = builder.eq("confidentialityLevel", "PRIVATE_ALL_AREAS");
        Op sameAreaDocs = builder.and(
            builder.eq("confidentialityLevel", "PRIVATE_ONLY_AREA"),
            builder.eq("area", area)
        );

        Op filter = builder.or(publicDocs, builder.or(allAreasDocs, sameAreaDocs));

        if ("DIRECTOR".equals(hierarchy)) {
            filter = builder.or(filter, builder.eq("confidentialityLevel", "CONFIDENTIAL"));
        }

        return filter.build();
    }

    private QueryChunk toChunk(Document document) {
        return new QueryChunk(
            document.getText(),
            document.getScore(),
            stringOf(document, "documentId"),
            stringOf(document, "title"),
            stringOf(document, "area"),
            stringOf(document, "confidentialityLevel"),
            stringOf(document, "chunkIndex")
        );
    }

    private String stringOf(Document document, String key) {
        Object value = document.getMetadata().get(key);
        return value == null ? null : String.valueOf(value);
    }
}
