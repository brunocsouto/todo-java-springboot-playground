package dev.souto.todo.controller;

import dev.souto.todo.entity.CategoryEntity;
import dev.souto.todo.entity.FolderEntity;
import dev.souto.todo.entity.TodoEntity;
import dev.souto.todo.repository.CategoryRepo;
import dev.souto.todo.repository.FolderRepo;
import dev.souto.todo.repository.TodoRepo;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

abstract class MongoIntegrationTestSupport {

    @Autowired
    protected CategoryRepo categoryRepo;

    @Autowired
    protected FolderRepo folderRepo;

    @Autowired
    protected TodoRepo todoRepo;

    @BeforeEach
    void cleanMongoCollections() {
        todoRepo.deleteAll();
        folderRepo.deleteAll();
        categoryRepo.deleteAll();
    }

    protected CategoryEntity persistCategory(String name) {
        return categoryRepo.save(
            new CategoryEntity(UUID.randomUUID().toString(), name)
        );
    }

    protected FolderEntity persistFolder(String name) {
        return folderRepo.save(
            new FolderEntity(UUID.randomUUID().toString(), name)
        );
    }

    protected TodoEntity persistTodo(
        String title,
        String description,
        CategoryEntity category,
        FolderEntity folder
    ) {
        return todoRepo.save(
            new TodoEntity(title, description, category, folder)
        );
    }
}
