package com.abenson.todo.service;

import com.abenson.todo.model.Todo;
import com.abenson.todo.model.TodoDTO;

import java.util.List;

public interface TodoService {

    List<TodoDTO> findAll();

    TodoDTO findById(Long id);

    TodoDTO saveTodo(TodoDTO todo);

    void deleteById(Long id);

    TodoDTO updateTodo(Long id, TodoDTO todo);

}
