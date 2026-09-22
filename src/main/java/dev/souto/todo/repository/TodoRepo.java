package dev.souto.todo.repository;

import dev.souto.todo.entity.TodoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepo extends MongoRepository<TodoEntity, String> {}
