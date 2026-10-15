package com.me.microservices.document_service.document.validator;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.me.microservices.document_service.document.entity.enums.TypeDocument;
import com.me.microservices.document_service.document.exception.InvalidDocumentTypeException;

@Component
public class DocumentValidatorFactory {

    private final Map<TypeDocument, DocumentValidator> validators;

    public DocumentValidatorFactory(List<DocumentValidator> validators) {
        this.validators = validators.stream()
                .collect(Collectors.toMap(
                        v -> v.getSupportedType(),
                        Function.identity()
                ));
    }

    public DocumentValidator getValidator(TypeDocument type) {

        DocumentValidator validator = validators.get(type);

        if (validator == null) {
            throw new InvalidDocumentTypeException("No validator available for type: " + type);
        }

        return validator;
    }

}
