package com.me.microservices.document_service.document.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "rejected_documents")
public class RejectedDocumentEntity {

    @Id
    private String id;

    private String documentId;

    private String title;

    private List<String> findings;

    private LocalDateTime createdAt;

    public static RejectedDocumentEntity create(
        String id,
        String title,
        List<String> findings
    ) {
        LocalDateTime now = LocalDateTime.now();

        return new RejectedDocumentEntity(
            null,
            id,
            title,
            findings,
            now
        );
    }
}
