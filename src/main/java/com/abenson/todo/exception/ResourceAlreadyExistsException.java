package com.abenson.todo.exception;

import lombok.Getter;

/**
 * Exception thrown when a resource already exists.
 * This exception should be caught and converted to a 409 Conflict response.
 */
@Getter
public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

}

