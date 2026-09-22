package dev.souto.todo.service;

import dev.souto.todo.dto.TodoCreateRequestDTO;
import dev.souto.todo.dto.TodoResponseDTO;
import dev.souto.todo.dto.TodoUpdateRequestDTO;
import dev.souto.todo.entity.CategoryEntity;
import dev.souto.todo.entity.FolderEntity;
import dev.souto.todo.entity.TodoEntity;
import dev.souto.todo.exception.BusinessRulesException;
import dev.souto.todo.pagination.PageResponse;
import dev.souto.todo.repository.CategoryRepo;
import dev.souto.todo.repository.FolderRepo;
import dev.souto.todo.repository.TodoRepo;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class TodoService {

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

    public PageResponse<TodoResponseDTO> findAll() {
        Page<TodoEntity> todoList = todoRepo.findAll(PageRequest.of(0, 10));

        Page<TodoResponseDTO> todoResponseDtoList = todoList.map(todo ->
            TodoResponseDTO.toDto(
                todo.getTitle(),
                todo.getDescription(),
                todo.getCategory(),
                todo.getFolder()
            )
        );

        return PageResponse.of(todoResponseDtoList);
    }

    public PageResponse<TodoResponseDTO> findAll(int page, int size) {
        Page<TodoEntity> todoList = todoRepo.findAll(
            PageRequest.of(page, size)
        );

        Page<TodoResponseDTO> todoResponseDtoList = todoList.map(todo ->
            TodoResponseDTO.toDto(
                todo.getTitle(),
                todo.getDescription(),
                todo.getCategory(),
                todo.getFolder()
            )
        );

        return PageResponse.of(todoResponseDtoList);
    }

    public TodoResponseDTO findById(UUID id) {
        TodoEntity todoEntity = findOrThrow(id);

        return TodoResponseDTO.toDto(
            todoEntity.getTitle(),
            todoEntity.getDescription(),
            todoEntity.getCategory(),
            todoEntity.getFolder()
        );
    }

    public TodoResponseDTO save(TodoCreateRequestDTO dto) {
        CategoryEntity categoryEntity = categoryRepo
            .findById(dto.categoryId())
            .orElseThrow(() ->
                new BusinessRulesException(
                    "There is no category with the id " + dto.categoryId()
                )
            );

        FolderEntity folderEntity = folderRepo
            .findById(dto.folderId())
            .orElseThrow(() ->
                new BusinessRulesException(
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

        return TodoResponseDTO.toDto(
            savedEntity.getTitle(),
            savedEntity.getDescription(),
            savedEntity.getCategory(),
            savedEntity.getFolder()
        );
    }

    @Transactional
    public TodoResponseDTO update(UUID id, TodoUpdateRequestDTO dto) {
        TodoEntity todoEntity = findOrThrow(id);

        FolderEntity folderEntity = folderRepo
            .findById(dto.folderId())
            .orElseThrow(() ->
                new BusinessRulesException("Folder does not exist")
            );

        CategoryEntity categoryEntity = categoryRepo
            .findById(dto.categoryId())
            .orElseThrow(() ->
                new BusinessRulesException("Category does not exist")
            );

        todoEntity.update(dto.title(), dto.description());
        todoEntity.bindCategory(categoryEntity);
        todoEntity.bindFolder(folderEntity);

        return TodoResponseDTO.toDto(
            todoEntity.getTitle(),
            todoEntity.getDescription(),
            categoryEntity,
            folderEntity
        );
    }

    @Transactional
    public void delete(UUID id) {
        TodoEntity todoEntity = findOrThrow(id);

        todoRepo.delete(todoEntity);
    }

    // Private methods
    private TodoEntity findOrThrow(UUID id) {
        return todoRepo
            .findById(id)
            .orElseThrow(() ->
                new BusinessRulesException("There is no todo with id " + id)
            );
    }
}
