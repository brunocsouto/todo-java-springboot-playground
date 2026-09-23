package dev.souto.todo.repository;

import dev.souto.todo.entity.TodoEntity;
import dev.souto.todo.entity.CategoryEntity;
import dev.souto.todo.entity.FolderEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepo extends MongoRepository<TodoEntity, String> {
    long deleteByFolder(FolderEntity folder);
    long deleteByCategory(CategoryEntity category);
}
