package com.abenson.todo.exception;

import lombok.Getter;

/**
 * Exception thrown when a requested resource is not found.
 * This exception should be caught and converted to a 404 Not Found response.
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);

    }

}

