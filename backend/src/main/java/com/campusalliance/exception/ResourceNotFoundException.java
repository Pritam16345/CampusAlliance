package com.campusalliance.exception;

/**
 * Thrown when we can't find something by ID. We use our own exception
 * instead of JPA's EntityNotFoundException so we have full control
 * over the message and don't depend on JPA specifics leaking out.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String entity, Long id) {
        super(entity + " not found with id: " + id);
    }
}
