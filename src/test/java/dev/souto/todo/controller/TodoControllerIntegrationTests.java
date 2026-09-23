package dev.souto.todo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.souto.todo.entity.CategoryEntity;
import dev.souto.todo.entity.FolderEntity;
import dev.souto.todo.entity.TodoEntity;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerIntegrationTests extends MongoIntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

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
    void shouldRejectTodoWithNullRequiredFields() throws Exception {
        mockMvc
            .perform(
                post("/api/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "title": null,
                          "folderId": null
                        }
                        """
                    )
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    void shouldRejectTodoWithWhitespaceOnlyTitle() throws Exception {
        mockMvc
            .perform(
                post("/api/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "title": "   ",
                          "folderId": "11111111-1111-1111-1111-111111111111"
                        }
                        """
                    )
            )
            .andExpect(status().isBadRequest())
            .andExpect(
                jsonPath("$.messages[0]").value("title: Title is required")
            );
    }

    @Test
    void shouldAcceptTodoTitleAndDescriptionAtMaximumLength() throws Exception {
        FolderEntity folder = persistFolder("Maximum length folder " + UUID.randomUUID());
        String title = "t".repeat(255);
        String description = "d".repeat(2000);

        mockMvc
            .perform(
                post("/api/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "title": "%s",
                          "description": "%s",
                          "folderId": "%s"
                        }
                        """.formatted(title, description, folder.getId())
                    )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value(title))
            .andExpect(jsonPath("$.description").value(description));
    }

    @Test
    void shouldRejectTodoTitleAndDescriptionAboveMaximumLength() throws Exception {
        FolderEntity folder = persistFolder("Over maximum folder " + UUID.randomUUID());
        String title = "t".repeat(256);
        String description = "d".repeat(2001);

        mockMvc
            .perform(
                post("/api/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "title": "%s",
                          "description": "%s",
                          "folderId": "%s"
                        }
                        """.formatted(title, description, folder.getId())
                    )
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
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
    void shouldTreatEmptyCategoryIdAsOptional() throws Exception {
        FolderEntity folder = persistFolder("Todo empty category " + UUID.randomUUID());

        mockMvc
            .perform(
                post("/api/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "title": "Todo with empty category",
                          "categoryId": "",
                          "folderId": "%s"
                        }
                        """.formatted(folder.getId())
                    )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.category").doesNotExist());
    }

    @Test
    void shouldRejectTodoWithInvalidCategoryId() throws Exception {
        FolderEntity folder = persistFolder("Todo invalid category " + UUID.randomUUID());

        mockMvc
            .perform(
                post("/api/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "title": "Todo with invalid category",
                          "categoryId": "not-a-valid-id",
                          "folderId": "%s"
                        }
                        """.formatted(folder.getId())
                    )
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
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
    void shouldRejectTodoWithMissingFolderReference() throws Exception {
        mockMvc
            .perform(
                post("/api/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "title": "Todo with missing folder",
                          "folderId": "11111111-1111-1111-1111-111111111111"
                        }
                        """
                    )
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldRejectNegativeTodoPage() throws Exception {
        mockMvc
            .perform(get("/api/todos").param("page", "-1"))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void shouldRejectZeroTodoPageSize() throws Exception {
        mockMvc
            .perform(get("/api/todos").param("size", "0"))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void shouldRejectExcessiveTodoPageSize() throws Exception {
        mockMvc
            .perform(get("/api/todos").param("size", "101"))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void shouldUpdateTodoWithoutCategory() throws Exception {
        FolderEntity originalFolder = persistFolder("Original todo folder " + UUID.randomUUID());
        FolderEntity updatedFolder = persistFolder("Updated todo folder " + UUID.randomUUID());
        TodoEntity todo = persistTodo(
            "Todo to update",
            "Before update",
            null,
            originalFolder
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

    @Test
    void shouldReturnTodosInAscendingTitleOrder() throws Exception {
        todoRepo.deleteAll();
        folderRepo.deleteAll();
        String suffix = UUID.randomUUID().toString();
        FolderEntity folder = persistFolder("Sort todo folder " + suffix);
        TodoEntity zebra = persistTodo("Sort todo Z " + suffix, null, null, folder);
        TodoEntity alpha = persistTodo("Sort todo A " + suffix, null, null, folder);
        TodoEntity middle = persistTodo("Sort todo M " + suffix, null, null, folder);

        mockMvc
            .perform(
                get("/api/todos")
                    .param("page", "0")
                    .param("size", "3")
                    .param("sort", "title,asc")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].title").value(alpha.getTitle()))
            .andExpect(jsonPath("$.content[1].title").value(middle.getTitle()))
            .andExpect(jsonPath("$.content[2].title").value(zebra.getTitle()));
    }

    @Test
    void shouldReturnTodosInDescendingTitleOrder() throws Exception {
        todoRepo.deleteAll();
        folderRepo.deleteAll();
        String suffix = UUID.randomUUID().toString();
        FolderEntity folder = persistFolder("Sort descending todo folder " + suffix);
        TodoEntity zebra = persistTodo(
            "Sort descending todo Z " + suffix,
            null,
            null,
            folder
        );
        TodoEntity alpha = persistTodo(
            "Sort descending todo A " + suffix,
            null,
            null,
            folder
        );
        TodoEntity middle = persistTodo(
            "Sort descending todo M " + suffix,
            null,
            null,
            folder
        );

        mockMvc
            .perform(
                get("/api/todos")
                    .param("page", "0")
                    .param("size", "3")
                    .param("sort", "title,desc")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].title").value(zebra.getTitle()))
            .andExpect(jsonPath("$.content[1].title").value(middle.getTitle()))
            .andExpect(jsonPath("$.content[2].title").value(alpha.getTitle()));
    }

    @Test
    void shouldRejectUnsupportedTodoSortField() throws Exception {
        mockMvc
            .perform(get("/api/todos").param("sort", "folder,asc"))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void shouldRejectUnsupportedTodoSortDirection() throws Exception {
        mockMvc
            .perform(get("/api/todos").param("sort", "title,sideways"))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.status").value(422));
    }
}
