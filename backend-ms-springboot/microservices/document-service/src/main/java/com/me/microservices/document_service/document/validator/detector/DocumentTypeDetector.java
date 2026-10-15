package com.me.microservices.document_service.document.validator.detector;

import java.io.IOException;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.me.microservices.document_service.document.exception.InvalidDocumentTypeException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class DocumentTypeDetector {

    private final Tika tika;

    public String detect(MultipartFile file) {

        try {
            return tika.detect(file.getInputStream(), file.getOriginalFilename());
        } catch (IOException exception) {
            throw new InvalidDocumentTypeException("Could not determine document type");
        }
    }
}
