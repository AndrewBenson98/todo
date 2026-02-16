package com.abenson.todo.controller;

import com.abenson.todo.model.TodoDTO;
import com.abenson.todo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class TodoController {

    private final TodoService todoService;


    public TodoController(@Autowired TodoService todoService) {
        this.todoService = todoService;
    }


    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("Todo API is running!");
    }


    /**
     * Get all todos
     * @return List of all todos
     */
    @GetMapping("/todos")
    public ResponseEntity<?> getAllTodos() {
        return ResponseEntity.ok(todoService.findAll());
    }

        /**
        * Get a todo by id
        * @param id The id of the todo to retrieve
        * @return The todo with the specified id
        */
        @GetMapping("/todos/{id}")
    public ResponseEntity<?> getTodoById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(todoService.findById(id));
    }


    /**
     * Delete a todo by id
     * @param id
     * @return 204 No Content if the todo was deleted successfully, or 404 Not Found if the todo with the specified id does not exist
     */
    @DeleteMapping("/todos/{id}")
    public ResponseEntity<?> deleteTodoById(@PathVariable("id") Long id) {
        todoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Create a new todo
     * @param todo The todo to create
     * @return The created todo
     */
    @PostMapping("/todos")
    public ResponseEntity<?> createTodo(@RequestBody TodoDTO todo) {
        System.out.println("In create todo controller method with todo: " + todo);
        return ResponseEntity.ok(todoService.saveTodo(todo));
    }

    /**
     * Update an existing todo
     * @param id The id of the todo to update
     * @param todo The updated todo data
     * @return The updated todo, or 404 Not Found if the todo with the specified id does not exist
     */
    @PutMapping("/todos/{id}")
    public ResponseEntity<?> updateTodo(@PathVariable("id") Long id, @RequestBody TodoDTO todo) {
        return ResponseEntity.ok(todoService.updateTodo(id, todo));
    }

}
