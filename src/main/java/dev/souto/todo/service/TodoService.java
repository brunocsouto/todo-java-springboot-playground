package dev.souto.todo.service;

import dev.souto.todo.dto.TodoCreateRequestDTO;
import dev.souto.todo.dto.TodoResponseDTO;
import dev.souto.todo.dto.TodoUpdateRequestDTO;
import dev.souto.todo.entity.CategoryEntity;
import dev.souto.todo.entity.FolderEntity;
import dev.souto.todo.entity.TodoEntity;
import dev.souto.todo.exception.ResourceNotFoundException;
import dev.souto.todo.repository.CategoryRepo;
import dev.souto.todo.repository.FolderRepo;
import dev.souto.todo.repository.TodoRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TodoService {

    private static final Logger logger = LoggerFactory.getLogger(
        TodoService.class
    );
    private final TodoRepo todoRepo;
    private final FolderRepo folderRepo;
    private final CategoryRepo categoryRepo;

    public TodoService(
        TodoRepo todoRepo,
        FolderRepo folderRepo,
        CategoryRepo categoryRepo
    ) {
        this.todoRepo = todoRepo;
        this.folderRepo = folderRepo;
        this.categoryRepo = categoryRepo;
    }

    public Page<TodoResponseDTO> findAll(Pageable pageable) {
        Page<TodoEntity> todoList = todoRepo.findAll(pageable);

        Page<TodoResponseDTO> todoResponseDtoList = todoList.map(todo ->
            TodoResponseDTO.toDto(
                todo.getTitle(),
                todo.getDescription(),
                todo.getCategory(),
                todo.getFolder()
            )
        );

        return todoResponseDtoList;
    }

    public TodoResponseDTO findById(String id) {
        TodoEntity todoEntity = findOrThrow(id);

        return TodoResponseDTO.toDto(
            todoEntity.getTitle(),
            todoEntity.getDescription(),
            todoEntity.getCategory(),
            todoEntity.getFolder()
        );
    }

    public TodoResponseDTO save(TodoCreateRequestDTO dto) {
        CategoryEntity categoryEntity = findCategoryOrNull(dto.categoryId());

        FolderEntity folderEntity = folderRepo
            .findById(dto.folderId())
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "There is no folder with the id " + dto.folderId()
                )
            );

        TodoEntity todoEntity = new TodoEntity(
            dto.title(),
            dto.description(),
            categoryEntity,
            folderEntity
        );

        TodoEntity savedEntity = todoRepo.save(todoEntity);

        logger.atInfo()
            .addKeyValue("todoId", savedEntity.getId())
            .log("Todo created");
        return TodoResponseDTO.toDto(
            savedEntity.getTitle(),
            savedEntity.getDescription(),
            savedEntity.getCategory(),
            savedEntity.getFolder()
        );
    }

    public TodoResponseDTO update(String id, TodoUpdateRequestDTO dto) {
        TodoEntity todoEntity = findOrThrow(id);

        FolderEntity folderEntity = folderRepo
            .findById(dto.folderId())
            .orElseThrow(() ->
                new ResourceNotFoundException("Folder does not exist")
            );

        CategoryEntity categoryEntity = findCategoryOrNull(dto.categoryId());

        todoEntity.update(dto.title(), dto.description());
        todoEntity.bindCategory(categoryEntity);
        todoEntity.bindFolder(folderEntity);
        TodoEntity savedEntity = todoRepo.save(todoEntity);

        logger.atInfo()
            .addKeyValue("todoId", savedEntity.getId())
            .log("Todo updated");
        return TodoResponseDTO.toDto(
            savedEntity.getTitle(),
            savedEntity.getDescription(),
            savedEntity.getCategory(),
            savedEntity.getFolder()
        );
    }

    public void delete(String id) {
        TodoEntity todoEntity = findOrThrow(id);

        todoRepo.delete(todoEntity);
        logger.atInfo()
            .addKeyValue("todoId", todoEntity.getId())
            .log("Todo deleted");
    }

    // Private methods
    private CategoryEntity findCategoryOrNull(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) {
            return null;
        }

        return categoryRepo
            .findById(categoryId)
            .orElseThrow(() ->
                new ResourceNotFoundException("Category does not exist")
            );
    }

    private TodoEntity findOrThrow(String id) {
        return todoRepo
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("There is no todo with id " + id)
            );
    }
}
