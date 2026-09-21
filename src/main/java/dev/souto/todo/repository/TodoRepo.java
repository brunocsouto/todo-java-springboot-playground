package dev.souto.todo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.souto.todo.entity.TodoEntity;

@Repository
public interface TodoRepo extends JpaRepository<TodoEntity, UUID> {
    TodoEntity findByTitle(String title);
}
