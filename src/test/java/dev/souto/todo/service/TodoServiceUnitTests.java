package dev.souto.todo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.souto.todo.dto.TodoCreateRequestDTO;
import dev.souto.todo.dto.TodoUpdateRequestDTO;
import dev.souto.todo.entity.CategoryEntity;
import dev.souto.todo.entity.FolderEntity;
import dev.souto.todo.entity.TodoEntity;
import dev.souto.todo.exception.ResourceNotFoundException;
import dev.souto.todo.repository.CategoryRepo;
import dev.souto.todo.repository.FolderRepo;
import dev.souto.todo.repository.TodoRepo;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TodoServiceUnitTests {

    @Mock
    private TodoRepo todoRepo;

    @Mock
    private FolderRepo folderRepo;

    @Mock
    private CategoryRepo categoryRepo;

    private TodoService service;

    @BeforeEach
    void setUp() {
        service = new TodoService(todoRepo, folderRepo, categoryRepo);
    }

    @Test
    void shouldCreateTodoWithFolderAndCategory() {
        FolderEntity folder = new FolderEntity("Work");
        CategoryEntity category = new CategoryEntity("Development");
        when(folderRepo.findById("folder-id")).thenReturn(Optional.of(folder));
        when(categoryRepo.findById("category-id")).thenReturn(Optional.of(category));
        when(todoRepo.save(any(TodoEntity.class))).thenAnswer(invocation ->
            invocation.getArgument(0)
        );

        var response = service.save(
            new TodoCreateRequestDTO(
                "Implement tests",
                "Add unit coverage",
                "category-id",
                "folder-id"
            )
        );

        assertEquals("Implement tests", response.title());
        assertEquals("Work", response.folder().name());
        assertEquals("Development", response.category().name());
        verify(todoRepo).save(any(TodoEntity.class));
    }

    @Test
    void shouldCreateTodoWithoutOptionalCategory() {
        FolderEntity folder = new FolderEntity("Personal");
        when(folderRepo.findById("folder-id")).thenReturn(Optional.of(folder));
        when(todoRepo.save(any(TodoEntity.class))).thenAnswer(invocation ->
            invocation.getArgument(0)
        );

        var response = service.save(
            new TodoCreateRequestDTO("Buy groceries", null, null, "folder-id")
        );

        assertEquals("Buy groceries", response.title());
        assertNull(response.category());
    }

    @Test
    void shouldRejectTodoWhenFolderDoesNotExist() {
        when(folderRepo.findById("missing-folder")).thenReturn(Optional.empty());

        assertThrows(
            ResourceNotFoundException.class,
            () ->
                service.save(
                    new TodoCreateRequestDTO(
                        "Invalid todo",
                        null,
                        null,
                        "missing-folder"
                    )
                )
        );
    }

    @Test
    void shouldRejectTodoWhenCategoryDoesNotExist() {
        when(categoryRepo.findById("missing-category")).thenReturn(
            Optional.empty()
        );

        assertThrows(
            ResourceNotFoundException.class,
            () ->
                service.save(
                    new TodoCreateRequestDTO(
                        "Invalid todo",
                        null,
                        "missing-category",
                        "folder-id"
                    )
                )
        );
    }

    @Test
    void shouldUpdateTodoAndRelationships() {
        FolderEntity originalFolder = new FolderEntity("Old folder");
        FolderEntity updatedFolder = new FolderEntity("New folder");
        CategoryEntity category = new CategoryEntity("Urgent");
        TodoEntity todo = new TodoEntity(
            "Old title",
            "Old description",
            null,
            originalFolder
        );
        when(todoRepo.findById("todo-id")).thenReturn(Optional.of(todo));
        when(folderRepo.findById("new-folder-id")).thenReturn(
            Optional.of(updatedFolder)
        );
        when(categoryRepo.findById("category-id")).thenReturn(
            Optional.of(category)
        );
        when(todoRepo.save(todo)).thenReturn(todo);

        var response = service.update(
            "todo-id",
            new TodoUpdateRequestDTO(
                "New title",
                "New description",
                "category-id",
                "new-folder-id"
            )
        );

        assertEquals("New title", response.title());
        assertEquals("New description", response.description());
        assertEquals("New folder", response.folder().name());
        assertEquals("Urgent", response.category().name());
    }

    @Test
    void shouldRejectUpdateWhenTodoDoesNotExist() {
        when(todoRepo.findById("missing-todo")).thenReturn(Optional.empty());

        assertThrows(
            ResourceNotFoundException.class,
            () ->
                service.update(
                    "missing-todo",
                    new TodoUpdateRequestDTO(
                        "Title",
                        null,
                        null,
                        "folder-id"
                    )
                )
        );
    }

    @Test
    void shouldDeleteExistingTodo() {
        TodoEntity todo = new TodoEntity(
            "Delete me",
            null,
            null,
            new FolderEntity("Work")
        );
        when(todoRepo.findById("todo-id")).thenReturn(Optional.of(todo));

        service.delete("todo-id");

        verify(todoRepo).delete(todo);
    }

    @Test
    void shouldRejectDeleteWhenTodoDoesNotExist() {
        when(todoRepo.findById("missing-todo")).thenReturn(Optional.empty());

        assertThrows(
            ResourceNotFoundException.class,
            () -> service.delete("missing-todo")
        );
    }
}
