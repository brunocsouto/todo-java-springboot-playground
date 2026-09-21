package dev.souto.todo.service;

import dev.souto.todo.entity.CategoryEntity;
import dev.souto.todo.entity.FolderEntity;
import dev.souto.todo.entity.TodoEntity;
import dev.souto.todo.dto.TodoRequestDTO;
import dev.souto.todo.dto.TodoResponseDTO;
import dev.souto.todo.repository.CategoryRepo;
import dev.souto.todo.repository.FolderRepo;
import dev.souto.todo.repository.TodoRepo;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
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

    public List<TodoResponseDTO> findAll() {
        List<TodoEntity> responseList = todoRepo.findAll();
        List<TodoResponseDTO> dtoList = responseList
            .stream()
            .map(todo ->
                TodoResponseDTO.toDto(
                    todo.getTitle(),
                    todo.getDescription(),
                    todo.getCategory().getName(),
                    todo.getFolder().getName()
                )
            )
            .toList();

        return dtoList;
    }

    public TodoResponseDTO findById(UUID id) {
        TodoEntity entity = findOrThrow(id);

        return TodoResponseDTO.toDto(
            entity.getTitle(),
            entity.getDescription(),
            entity.getCategory().getName(),
            entity.getFolder().getName()
        );
    }

    public TodoResponseDTO findByTitle(String title) {
        TodoEntity entity = todoRepo.findByTitle(title);

        return TodoResponseDTO.toDto(
            entity.getTitle(),
            entity.getDescription(),
            entity.getCategory().getName(),
            entity.getFolder().getName()
        );
    }

    @Transactional
    public TodoResponseDTO save(TodoRequestDTO dto) {
        FolderEntity folder = folderRepo
            .findByName(dto.folderName())
            .orElseThrow(() ->
                new RuntimeException(
                    "Folder " + dto.folderName() + " does not exist"
                )
            );

        CategoryEntity category = categoryRepo
            .findByName(dto.categoryName())
            .orElseThrow(() ->
                new RuntimeException(
                    "Category " + dto.categoryName() + " does not exist"
                )
            );

        TodoEntity todo = TodoRequestDTO.toEntity(dto, category, folder);

        if (folder.getName() == dto.folderName()) {
            throw new RuntimeException(
                "The folder must be different to update"
            );
        }

        if (category.getName() == dto.categoryName()) {
            throw new RuntimeException(
                "The category must be different to update"
            );
        }

        todo.setFolder(folder);
        todo.setCategory(category);

        TodoEntity savedEntity = todoRepo.save(todo);

        return TodoResponseDTO.toDto(
            savedEntity.getTitle(),
            savedEntity.getDescription(),
            savedEntity.getCategory().getName(),
            savedEntity.getFolder().getName()
        );
    }

    @Transactional
    public TodoResponseDTO update(UUID id, TodoRequestDTO dto) {
        TodoEntity todoEntity = findOrThrow(id);

        if (todoEntity.getTitle() == dto.title()) {
            throw new RuntimeException("The title must be different to update");
        }

        if (todoEntity.getDescription() == dto.description()) {
            throw new RuntimeException(
                "The description must be different to update"
            );
        }

        FolderEntity folder = folderRepo
            .findByName(dto.folderName())
            .orElseThrow(() ->
                new RuntimeException(
                    "Folder " + dto.folderName() + " does not exist"
                )
            );

        CategoryEntity category = categoryRepo
            .findByName(dto.categoryName())
            .orElseThrow(() ->
                new RuntimeException(
                    "Category " + dto.categoryName() + " does not exist"
                )
            );

        if (category.getName() == dto.categoryName()) {
            throw new RuntimeException(
                "The category must be different to update"
            );
        }

        if (folder.getName() == dto.folderName()) {
            throw new RuntimeException(
                "The folder must be different to update"
            );
        }

        todoEntity.setTitle(dto.title());
        todoEntity.setDescription(dto.description());
        todoEntity.setCategory(category);
        todoEntity.setFolder(folder);

        TodoEntity updatedEntity = todoRepo.save(todoEntity);

        return TodoResponseDTO.toDto(
            updatedEntity.getTitle(),
            updatedEntity.getDescription(),
            updatedEntity.getCategory().getName(),
            updatedEntity.getFolder().getName()
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
                new RuntimeException("There is no todo with id " + id)
            );
    }
}
