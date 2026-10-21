package com.me.microservices.rag_service.ingest.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.me.microservices.rag_service.document.client.DocumentClient;
import com.me.microservices.rag_service.document.dto.DocumentMetadata;
import com.me.microservices.rag_service.document.service.DocumentService;
import com.me.microservices.rag_service.ingest.dto.response.IngestDocumentResponse;
import com.me.microservices.rag_service.ingest.exception.DocumentTextExtractionException;
import com.me.microservices.rag_service.ingest.exception.RemoteDocumentNotFoundException;
import com.me.microservices.rag_service.ingest.exception.VectorStoreException;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class IngestService {

    private final DocumentClient documentClient;
    private final DocumentService documentService;
    private final JdbcTemplate jdbcTemplate;
    private final VectorStore vectorStore;

    @Value("${spring.ai.vectorstore.pgvector.table-name:vector_store}")
    private String vectorTable;

    public IngestDocumentResponse ingest(UUID id, String authorization, boolean force) {
        if (force) {
            deleteVectors(id);
        } else if (isIndexed(id)) {
            return new IngestDocumentResponse(id.toString(), "ALREADY_INDEXED", null, null, null);
        }

        DocumentMetadata metadata = fetchMetadata(id, authorization);
        Resource resource = documentService.getContent(id, authorization);
        String text = extractText(resource, metadata);
        List<Document> chunks = split(text, metadata);
        store(chunks);

        return new IngestDocumentResponse(id.toString(), "INDEXED", metadata, text.length(), chunks.size());
    }

    public boolean isIndexed(UUID id) {
        Boolean exists = jdbcTemplate.queryForObject(
            "SELECT EXISTS(SELECT 1 FROM " + vectorTable + " WHERE metadata ->> 'documentId' = ?)",
            Boolean.class,
            id.toString()
        );
        return Boolean.TRUE.equals(exists);
    }

    public void updateConfidentiality(UUID id, String confidentialityLevel) {
        try {
            jdbcTemplate.update(
                "UPDATE " + vectorTable
                    + " SET metadata = (jsonb_set(metadata::jsonb, '{confidentialityLevel}', to_jsonb(?::text)))::json"
                    + " WHERE metadata ->> 'documentId' = ?",
                confidentialityLevel,
                id.toString()
            );
        } catch (RuntimeException exception) {
            throw new VectorStoreException(
                "Could not update confidentiality metadata for document " + id + ": " + exception.getMessage());
        }
    }

    private DocumentMetadata fetchMetadata(UUID id, String authorization) {
        try {
            return documentClient.getMetadata(id, authorization);
        } catch (FeignException.NotFound exception) {
            throw new RemoteDocumentNotFoundException("Document not found in document-service: " + id);
        }
    }

    private String extractText(Resource resource, DocumentMetadata metadata) {
        if (!"PDF".equalsIgnoreCase(metadata.type())) {
            throw new DocumentTextExtractionException(
                "Only PDF documents can be ingested, got: " + metadata.type());
        }

        try (InputStream input = resource.getInputStream();
             PDDocument document = Loader.loadPDF(input.readAllBytes())) {
            String text = new PDFTextStripper().getText(document);

            if (text == null || text.isBlank()) {
                throw new DocumentTextExtractionException(
                    "Document has no extractable text (possibly a scanned image-only PDF): " + metadata.id());
            }

            return text;
        } catch (IOException exception) {
            throw new DocumentTextExtractionException(
                "Could not extract text from document: " + metadata.id());
        }
    }

    private List<Document> split(String text, DocumentMetadata metadata) {
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> chunks = splitter.apply(List.of(new Document(text)));

        for (int i = 0; i < chunks.size(); i++) {
            chunks.get(i).getMetadata().put("documentId", metadata.id());
            chunks.get(i).getMetadata().put("title", metadata.title());
            chunks.get(i).getMetadata().put("area", metadata.area());
            chunks.get(i).getMetadata().put("confidentialityLevel", metadata.confidentialityLevel());
            chunks.get(i).getMetadata().put("type", metadata.type());
            chunks.get(i).getMetadata().put("chunkIndex", i);
        }

        return chunks;
    }

    private void deleteVectors(UUID id) {
        try {
            vectorStore.delete(new FilterExpressionBuilder().eq("documentId", id.toString()).build());
        } catch (RuntimeException exception) {
            throw new VectorStoreException(
                "Could not clear vectors for document " + id + ": " + exception.getMessage());
        }
    }

    private void store(List<Document> chunks) {
        try {
            vectorStore.add(chunks);
        } catch (RuntimeException exception) {
            throw new VectorStoreException("Could not store vectors: " + exception.getMessage());
        }
    }
}
