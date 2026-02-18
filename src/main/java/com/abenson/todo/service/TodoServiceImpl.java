package com.abenson.todo.service;

import com.abenson.todo.exception.ResourceAlreadyExistsException;
import com.abenson.todo.exception.ResourceNotFoundException;
import com.abenson.todo.mappers.TodoMapper;
import com.abenson.todo.model.Todo;
import com.abenson.todo.model.TodoDTO;
import com.abenson.todo.repository.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing todos
 */
@Service
@Slf4j
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;

    /**
     * Constructor for TodoServiceImpl
     * @param todoRepository The repository to manage todo data, injected by Spring
     * @param todoMapper The mapper to convert between Todo and TodoDTO, injected by Spring
     */
   public TodoServiceImpl(@Autowired TodoRepository todoRepository, @Autowired TodoMapper todoMapper) {
       this.todoRepository = todoRepository;
       this.todoMapper = todoMapper;
   }

    @Override
    public List<TodoDTO> findAll() {

       log.info("Find all todos");
       List<TodoDTO> todos = todoRepository.findAll().stream().map(todoMapper::toDto).toList();
       log.info("Found {} todos", todos.size());
       return todos;
    }

    @Override
    public TodoDTO findById(Long id) {

       Optional<Todo> optTodo = todoRepository.findById(id);
       if(optTodo.isPresent()) {
           log.debug("Found todo with id: {}", id);
              return todoMapper.toDto(optTodo.get());
       }else{
              throw new ResourceNotFoundException("Resource not found with id: " + id);
       }

    }

    @Override
    public TodoDTO saveTodo(TodoDTO todoDto) {
       Todo todo = todoMapper.toEntity(todoDto);

       Optional<Todo> optTodo = todoRepository.findByTitleAndDescription(todo.getTitle(), todo.getDescription());
         if(optTodo.isPresent()) {
             log.warn("Todo with the same title and description already exists");
             throw new ResourceAlreadyExistsException("Todo with the same title and description already exists");
         }


         todo.setCompleted(false);
         todo.setCreatedAt(LocalDateTime.now());
         Todo savedTodo = todoRepository.save(todo);
         log.info("Todo with id: {} created successfully", savedTodo.getId());

        return todoMapper.toDto(savedTodo);
    }

    @Override
    public void deleteById(Long id) {
        Optional<Todo> optTodo = todoRepository.findById(id);
        if(optTodo.isPresent()) {
            log.warn("Deleting todo with id: {}", id);
            todoRepository.deleteById(id);
        }else{
            log.warn("Todo with id: {} not found for deletion", id);
            throw new ResourceNotFoundException("Resource not found with id: " + id);
        }

    }

    @Override
    public TodoDTO updateTodo(Long id, TodoDTO todoDto) {
       Todo todo = todoMapper.toEntity(todoDto);


        Optional<Todo> optTodo = todoRepository.findById(id);
        if(optTodo.isPresent()) {

            Todo existingTodo = optTodo.get();
            log.info("Updating todo with id: {}", id);
            if(todo.getTitle() != null)
                existingTodo.setTitle(todo.getTitle());

            if(todo.getDescription() != null)
                existingTodo.setDescription(todo.getDescription());

            if(todo.getCompleted() != null)
                existingTodo.setCompleted(todo.getCompleted());

            todoRepository.save(existingTodo );
            log.info("Todo with id: {} updated successfully", id);
            return todoMapper.toDto(existingTodo);
        }else{
            log.warn("Todo with id: {} not found for update", id);
            throw new ResourceNotFoundException("Resource not found with id: " + id);
        }
    }
}
