package com.abenson.todo.mappers;

import com.abenson.todo.model.Todo;
import com.abenson.todo.model.TodoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TodoMapper {


    @Mapping(source = "completed", target = "completed")
    TodoDTO toDto(Todo todo);

    Todo toEntity(TodoDTO dto);

}
