package com.me.microservices.document_service.document.validator;

import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.me.microservices.document_service.document.entity.enums.TypeDocument;
import com.me.microservices.document_service.document.exception.InvalidDocumentTypeException;

@Component
public class PdfDocumentValidator implements DocumentValidator {

    @Override
    public TypeDocument getSupportedType() {
        return TypeDocument.PDF;
    }
    
    @Override
    public void validate(MultipartFile file) {
        
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {

            if (document.getNumberOfPages() == 0) {
                throw new IllegalArgumentException("PDF must contain at least one page");
            }

        } catch (IOException exception) {
            throw new InvalidDocumentTypeException("Invalid or corrupted PDF file");
        }
    }

}
