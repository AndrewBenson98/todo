# GlobalExceptionHandler Implementation Guide

## Overview
The `GlobalExceptionHandler` is a centralized exception handling mechanism for your Spring Boot REST API. It uses the `@RestControllerAdvice` annotation to intercept exceptions across all controllers and return consistent, well-formatted error responses.

## Architecture

### Files Created
1. **GlobalExceptionHandler.java** - Central exception handler with `@RestControllerAdvice`
2. **ApiErrorResponse.java** - Standard error response DTO
3. **ResourceNotFoundException.java** - Custom exception for 404 errors
4. **ResourceAlreadyExistsException.java** - Custom exception for 409 conflicts

## SOLID Principles Applied

### Single Responsibility Principle (SRP)
- `GlobalExceptionHandler`: Only handles exception conversion to responses
- `ApiErrorResponse`: Only represents error response data
- Each exception class: Represents a specific error condition

### Open/Closed Principle (OCP)
- New exception types can be easily added by creating new handler methods
- No need to modify existing exception handlers
- Example: Adding `@ExceptionHandler(MyCustomException.class)` for new exceptions

### Liskov Substitution Principle (LSP)
- All custom exceptions extend `RuntimeException`
- They can be used anywhere a `RuntimeException` is expected
- Properly implement the exception contract (message and context)

### Interface Segregation Principle (ISP)
- `ApiErrorResponse` exposes only necessary error fields
- Handler methods are focused and specific to their exception type
- Clients receive exactly what they need

### Dependency Inversion Principle (DIP)
- Handlers depend on the abstract `Exception` type, not concrete implementations
- Spring manages the dependency injection
- Logging abstraction (SLF4J via Lombok's `@Slf4j`)

## Exception Handlers Explained

### 1. ResourceNotFoundException (404)
```java
@ExceptionHandler(ResourceNotFoundException.class)
```
**When it's thrown**: When a resource cannot be found in the database
**HTTP Status**: 404 Not Found
**Example**: `GET /api/v1/todos/999` (if todo doesn't exist)

### 2. ResourceAlreadyExistsException (409)
```java
@ExceptionHandler(ResourceAlreadyExistsException.class)
```
**When it's thrown**: When trying to create a resource that already exists
**HTTP Status**: 409 Conflict
**Example**: `POST /api/v1/todos` (if todo ID already exists)

### 3. MethodArgumentNotValidException (400)
```java
@ExceptionHandler(MethodArgumentNotValidException.class)
```
**When it's thrown**: When request body validation fails (`@NotNull`, `@NotBlank`, etc.)
**HTTP Status**: 400 Bad Request
**Returns**: Individual field validation errors

### 4. NoHandlerFoundException (404)
```java
@ExceptionHandler(NoHandlerFoundException.class)
```
**When it's thrown**: When requesting a non-existent endpoint
**HTTP Status**: 404 Not Found

### 5. IllegalArgumentException (400)
```java
@ExceptionHandler(IllegalArgumentException.class)
```
**When it's thrown**: When an invalid argument is provided to a method
**HTTP Status**: 400 Bad Request

### 6. Generic Exception (500)
```java
@ExceptionHandler(Exception.class)
```
**When it's thrown**: Any unhandled exception
**HTTP Status**: 500 Internal Server Error
**Note**: This is a catch-all; specific exceptions should be handled above this

## API Error Response Format

All error responses follow this consistent format:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Todo not found with id : '999'",
  "path": "/api/v1/todos/999",
  "timestamp": "2026-02-14T10:30:00",
  "validationErrors": null
}
```

### With Validation Errors:
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/todos",
  "timestamp": "2026-02-14T10:30:00",
  "validationErrors": {
    "title": "must not be blank",
    "description": "must not be blank"
  }
}
```

## Usage Examples

### Example 1: Handling Not Found
```java
// Controller
@GetMapping("/todos/{id}")
public ResponseEntity<?> getTodoById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(todoService.findById(id));
}

// Service throws exception
if (todo is not found) {
    throw new ResourceNotFoundException("Todo", "id", id);
}

// GlobalExceptionHandler catches it and returns:
// Status: 404
// Body: { "status": 404, "error": "Not Found", "message": "..." }
```

### Example 2: Validation Error
```java
// Controller
@PostMapping("/todos")
public ResponseEntity<?> createTodo(@Valid @RequestBody Todo todo) {
    return ResponseEntity.ok(todoService.saveTodo(todo));
}

// If @NotBlank validation fails on title field:
// GlobalExceptionHandler catches it and returns:
// Status: 400
// Body: { "status": 400, ..., "validationErrors": { "title": "must not be blank" } }
```

## Configuration Requirements

### Enable 404 Handling (Optional)
To handle 404s for non-existent endpoints, add to `application.properties`:

```properties
server.error.include-message=always
server.error.include-binding-errors=always
server.error.include-stacktrace=on-param
server.servlet.throw-exception-if-no-handler-found=true
spring.web.resources.add-mappings=false
```

## Best Practices Implemented

1. **Logging**: All exceptions are logged appropriately (warn for expected errors, error for unexpected ones)
2. **Sensitive Information**: Generic messages for 500 errors to avoid exposing internal details
3. **Consistency**: All responses follow the same structure
4. **HTTP Status Codes**: Proper RESTful status codes used
5. **Timestamp**: Every error response includes when it occurred
6. **Path Information**: Request path is included for debugging
7. **Extensibility**: Easy to add new exception handlers

## Adding a New Exception Handler

1. Create a custom exception class:
```java
public class CustomException extends RuntimeException {
    // Constructor and fields
}
```

2. Add handler method in `GlobalExceptionHandler`:
```java
@ExceptionHandler(CustomException.class)
public ResponseEntity<ApiErrorResponse> handleCustomException(
        CustomException ex,
        WebRequest request) {
    log.warn("Custom exception: {}", ex.getMessage());
    
    ApiErrorResponse response = ApiErrorResponse.of(
            HttpStatus.YOUR_STATUS,
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
    );
    
    return new ResponseEntity<>(response, HttpStatus.YOUR_STATUS);
}
```

3. Use the exception in your service layer:
```java
if (condition) {
    throw new CustomException("Your custom message");
}
```

## Testing

Example test case:
```java
@Test
void testResourceNotFoundHandling() {
    // When
    ResourceNotFoundException ex = assertThrows(
        ResourceNotFoundException.class,
        () -> todoService.findById(999L)
    );
    
    // Then
    assertTrue(ex.getMessage().contains("not found"));
    assertEquals("Todo", ex.getResourceName());
    assertEquals("id", ex.getFieldName());
}
```

## Integration with TodoService

The `TodoServiceImpl` has been updated to use custom exceptions instead of generic `RuntimeException`:

- `findById()` → throws `ResourceNotFoundException`
- `saveTodo()` → throws `ResourceAlreadyExistsException`
- `deleteById()` → throws `ResourceNotFoundException`
- `updateTodo()` → throws `ResourceNotFoundException`

## Benefits

✅ Centralized exception handling  
✅ Consistent API error responses  
✅ Better error messages for API consumers  
✅ Proper HTTP status codes  
✅ Easy to extend with new exception types  
✅ Follows Spring Boot best practices  
✅ SOLID principles compliance  
✅ Separation of concerns  
✅ Improved debugging with structured error information  
✅ Production-ready error handling  


