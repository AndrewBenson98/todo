package com.abenson.todo.controller;

import com.abenson.todo.model.TodoDTO;
import com.abenson.todo.service.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class TodoController {

    // The service to handle todo operations, injected by Spring
    private final TodoService todoService;

    /**
     * Constructor for TodoController
     * @param todoService The service to handle todo operations, injected by Spring
     */
    public TodoController(@Autowired TodoService todoService) {
        this.todoService = todoService;
    }


    /**
     * Get the status of the API
     * @return A message indicating that the API is running
     */
    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        log.info("Received request for API status");
        return ResponseEntity.ok("Todo API is running!");
    }


    /**
     * Get all todos
     * @return List of all todos
     */
    @GetMapping("/todo")
    public ResponseEntity<?> getAllTodos() {
        log.info("Received request for API todos");
        return ResponseEntity.ok(todoService.findAll());
    }

        /**
        * Get a todo by id
        * @param id The id of the todo to retrieve
        * @return The todo with the specified id
        */
        @GetMapping("/todo/{id}")
    public ResponseEntity<?> getTodoById(@PathVariable("id") Long id) {
            log.info("Received request for API todo with id: {}", id);
        return ResponseEntity.ok(todoService.findById(id));
    }


    /**
     * Delete a todo by id
     * @param id The id of the todo to delete
     * @return 204 No Content if the todo was deleted successfully, or 404 Not Found if the todo with the specified id does not exist
     */
    @DeleteMapping("/todo/{id}")
    public ResponseEntity<?> deleteTodoById(@PathVariable("id") Long id) {
        log.info("Received request to delete API todo with id: {}", id);
        todoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Create a new todo
     * @param todo The todo to create
     * @return The created todo
     */
    @PostMapping("/todo")
    public ResponseEntity<?> createTodo(@RequestBody TodoDTO todo) {
        log.info("Received request to create API todo: {}", todo);
        return ResponseEntity.ok(todoService.saveTodo(todo));
    }

    /**
     * Update an existing todo
     * @param id The id of the todo to update
     * @param todo The updated todo data
     * @return The updated todo, or 404 Not Found if the todo with the specified id does not exist
     */
    @PutMapping("/todo/{id}")
    public ResponseEntity<?> updateTodo(@PathVariable("id") Long id, @RequestBody TodoDTO todo) {
        log.info("Received request to update API todo with id: {} and data: {}", id, todo);
        return ResponseEntity.ok(todoService.updateTodo(id, todo));
    }

}
