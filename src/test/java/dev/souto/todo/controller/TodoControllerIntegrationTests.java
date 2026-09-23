package dev.souto.todo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.souto.todo.entity.CategoryEntity;
import dev.souto.todo.entity.FolderEntity;
import dev.souto.todo.entity.TodoEntity;
import dev.souto.todo.repository.CategoryRepo;
import dev.souto.todo.repository.FolderRepo;
import dev.souto.todo.repository.TodoRepo;
import java.lang.reflect.Field;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private FolderRepo folderRepo;

    @Autowired
    private TodoRepo todoRepo;

    private CategoryEntity persistCategory(String name) throws Exception {
        CategoryEntity category = new CategoryEntity(name);
        Field field = CategoryEntity.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(category, UUID.randomUUID().toString());
        return categoryRepo.save(category);
    }

    private FolderEntity persistFolder(String name) throws Exception {
        FolderEntity folder = new FolderEntity(name);
        Field field = FolderEntity.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(folder, UUID.randomUUID().toString());
        return folderRepo.save(folder);
    }

    @Test
    void shouldCreateTodoWithExistingRelationships() throws Exception {
        CategoryEntity category = persistCategory("Integration category " + UUID.randomUUID());
        FolderEntity folder = persistFolder("Integration folder " + UUID.randomUUID());

        mockMvc
            .perform(
                post("/api/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "title": "Integration todo",
                          "description": "Created by an integration test.",
                          "categoryId": "%s",
                          "folderId": "%s"
                        }
                        """.formatted(category.getId(), folder.getId())
                    )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Integration todo"))
            .andExpect(jsonPath("$.category.name").value(category.getName()))
            .andExpect(jsonPath("$.folder.name").value(folder.getName()));
    }

    @Test
    void shouldRejectTodoWithoutFolderId() throws Exception {
        mockMvc
            .perform(
                post("/api/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "title": "Invalid integration todo",
                          "description": "Missing relationship IDs."
                        }
                        """
                    )
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").value("Validation error"))
            .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    void shouldRejectTodoWithNonExistingFolder() throws Exception {
        CategoryEntity category = persistCategory("Existing category " + UUID.randomUUID());

        mockMvc
            .perform(
                post("/api/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "title": "Todo with invalid relationships",
                          "description": "References records that do not exist.",
                          "categoryId": "%s",
                          "folderId": "11111111-1111-1111-1111-111111111111"
                        }
                        """.formatted(category.getId())
                    )
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.detail").value("Resource not found"))
            .andExpect(
                jsonPath("$.messages[0]").value(
                    "There is no folder with the id 11111111-1111-1111-1111-111111111111"
                )
            );
    }

    @Test
    void shouldReturnNotFoundWhenTodoDoesNotExist() throws Exception {
        mockMvc
            .perform(get("/api/todos/{id}", UUID.randomUUID().toString()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.detail").value("Resource not found"))
            .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    void shouldCreateTodoWithoutCategory() throws Exception {
        FolderEntity folder = persistFolder("Todo folder without category " + UUID.randomUUID());

        mockMvc
            .perform(
                post("/api/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "title": "Todo without category",
                          "folderId": "%s"
                        }
                        """.formatted(folder.getId())
                    )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Todo without category"))
            .andExpect(jsonPath("$.category").doesNotExist())
            .andExpect(jsonPath("$.folder.name").value(folder.getName()));
    }

    @Test
    void shouldRejectTodoWithMalformedFolderId() throws Exception {
        mockMvc
            .perform(
                post("/api/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "title": "Invalid folder id",
                          "folderId": "not-a-uuid"
                        }
                        """
                    )
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").value("Validation error"))
            .andExpect(
                jsonPath("$.messages[0]").value(
                    "folderId: Folder id must be a valid UUID"
                )
            );
    }

    @Test
    void shouldUpdateTodoWithoutCategory() throws Exception {
        FolderEntity originalFolder = persistFolder("Original todo folder " + UUID.randomUUID());
        FolderEntity updatedFolder = persistFolder("Updated todo folder " + UUID.randomUUID());
        TodoEntity todo = todoRepo.save(
            new TodoEntity(
                "Todo to update",
                "Before update",
                null,
                originalFolder
            )
        );

        mockMvc
            .perform(
                put("/api/todos/{id}", todo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "title": "Updated todo",
                          "description": "After update",
                          "folderId": "%s"
                        }
                        """.formatted(updatedFolder.getId())
                    )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated todo"))
            .andExpect(jsonPath("$.category").doesNotExist())
            .andExpect(jsonPath("$.folder.name").value(updatedFolder.getName()));
    }

    @Test
    void shouldReturnPagedTodos() throws Exception {
        persistFolder("Todo list folder " + UUID.randomUUID());

        mockMvc
            .perform(get("/api/todos?page=0&size=1&sort=title,asc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.number").value(0))
            .andExpect(jsonPath("$.size").value(1))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.totalElements").isNumber());
    }
}
