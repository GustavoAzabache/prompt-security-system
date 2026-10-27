package com.me.microservices.document_service.document.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.me.microservices.document_service.document.entity.enums.ConfidentialityLevel;
import com.me.microservices.document_service.document.entity.enums.DocumentStatus;
import com.me.microservices.document_service.document.entity.enums.TypeDocument;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "documents")
public class DocumentEntity {

    @Id
    private String id;

    private String title;

    private long size;

    private TypeDocument type;

    private String mediaType;

    private String location;

    private String area;

    private String hierarchy;

    private ConfidentialityLevel confidentialityLevel;
    
    private DocumentStatus status;

    @CreatedDate 
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static DocumentEntity create(
        UUID id,
        String title,
        long size,
        TypeDocument type,
        String mediaType,
        String location,
        String area,
        String hierarchy
    ) {
        LocalDateTime now = LocalDateTime.now();

        return new DocumentEntity(
            id.toString(),
            title,
            size,
            type,
            mediaType,
            location,
            area,
            hierarchy,
            ConfidentialityLevel.PRIVATE_ONLY_AREA,
            DocumentStatus.PENDING,
            now,
            now
        );
    }

    public void changeConfidentiality(ConfidentialityLevel confidentialityLevel) {
        if (confidentialityLevel == null) return ;
        if (this.confidentialityLevel == confidentialityLevel) return;

        this.confidentialityLevel = confidentialityLevel;
    }

    public void accept() {
        this.status = DocumentStatus.READY;
    }

    public void reject() {
        this.status = DocumentStatus.REJECTED;
    }

}
