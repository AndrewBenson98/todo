package com.abenson.todo.model;

public record TodoDTO (
        Long id,
        String title,
        String description,
        Boolean completed
){}
