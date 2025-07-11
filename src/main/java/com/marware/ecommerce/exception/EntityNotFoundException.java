package com.marware.ecommerce.exception;

public class EntityNotFoundException extends ApiException {
    public EntityNotFoundException(String entityName, Long id) {
        super("ENTITY_NOT_FOUND",
                String.format("%s con ID %d no encontrado", entityName, id));
    }
}
