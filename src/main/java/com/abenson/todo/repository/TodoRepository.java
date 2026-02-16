package com.abenson.todo.repository;

import com.abenson.todo.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo,Long> {
    Optional<Todo> findByTitleAndDescription(String title, String description);
}
