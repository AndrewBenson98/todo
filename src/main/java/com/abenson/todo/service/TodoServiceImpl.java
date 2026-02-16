package com.abenson.todo.service;

import com.abenson.todo.exception.ResourceAlreadyExistsException;
import com.abenson.todo.exception.ResourceNotFoundException;
import com.abenson.todo.mappers.TodoMapper;
import com.abenson.todo.model.Todo;
import com.abenson.todo.model.TodoDTO;
import com.abenson.todo.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;

   public TodoServiceImpl(@Autowired TodoRepository todoRepository, @Autowired TodoMapper todoMapper) {

       this.todoRepository = todoRepository;
       this.todoMapper = todoMapper;
   }

    @Override
    public List<TodoDTO> findAll() {

       return todoRepository.findAll().stream().map(todoMapper::toDto).toList();
    }

    @Override
    public TodoDTO findById(Long id) {

       Optional<Todo> optTodo = todoRepository.findById(id);
       if(optTodo.isPresent()) {
              return todoMapper.toDto(optTodo.get());
       }else{
              throw new ResourceNotFoundException("Resource not found with id: " + id);
       }

    }

    @Override
    public TodoDTO saveTodo(TodoDTO todoDto) {

       System.out.println("Saving todo: " + todoDto);
       Todo todo = todoMapper.toEntity(todoDto);

       Optional<Todo> optTodo = todoRepository.findByTitleAndDescription(todo.getTitle(), todo.getDescription());
         if(optTodo.isPresent()) {
             throw new ResourceAlreadyExistsException("Todo with the same title and description already exists");
         }

        todo.setCompleted(false);
         todo.setCreatedAt(LocalDateTime.now());

        Todo savedTodo = todoRepository.save(todo);
         System.out.println("Saved todo: " + savedTodo);

         TodoDTO savedTodoDTO = todoMapper.toDto(savedTodo);


         System.out.println("Saved todoDTO: " + savedTodoDTO);


         return savedTodoDTO;
    }

    @Override
    public void deleteById(Long id) {
        Optional<Todo> optTodo = todoRepository.findById(id);
        if(optTodo.isPresent()) {
            todoRepository.deleteById(id);
        }else{
            throw new ResourceNotFoundException("Resource not found with id: " + id);
        }

    }

    @Override
    public TodoDTO updateTodo(Long id, TodoDTO todoDto) {
       Todo todo = todoMapper.toEntity(todoDto);

        Optional<Todo> optTodo = todoRepository.findById(id);
        if(optTodo.isPresent()) {
            Todo existingTodo = optTodo.get();
            existingTodo.setTitle(todo.getTitle());
            existingTodo.setDescription(todo.getDescription());
            existingTodo.setCompleted(todo.getCompleted());
            todoRepository.save(existingTodo );
            return todoMapper.toDto(existingTodo);
        }else{
            throw new ResourceNotFoundException("Resource not found with id: " + id);
        }
    }
}
