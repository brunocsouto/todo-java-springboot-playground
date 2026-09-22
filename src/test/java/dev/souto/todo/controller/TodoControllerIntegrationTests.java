package dev.souto.todo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import dev.souto.todo.entity.CategoryEntity;
import dev.souto.todo.entity.FolderEntity;
import dev.souto.todo.repository.CategoryRepo;
import dev.souto.todo.repository.FolderRepo;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private FolderRepo folderRepo;

    @Test
    void shouldCreateTodoWithExistingRelationships() throws Exception {
        CategoryEntity category = categoryRepo.save(
            new CategoryEntity("Integration category " + UUID.randomUUID())
        );
        FolderEntity folder = folderRepo.save(
            new FolderEntity("Integration folder " + UUID.randomUUID())
        );

        mockMvc
            .perform(
                post("/api/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "title": "Integration todo",
                          "description": "Created by an integration test.",
                          "categoryId": "%s",
                          "folderId": "%s"
                        }
                        """.formatted(category.getId(), folder.getId()))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Integration todo"))
            .andExpect(jsonPath("$.category.name").value(category.getName()))
            .andExpect(jsonPath("$.folder.name").value(folder.getName()));
    }

    @Test
    void shouldRejectTodoWithoutRelationshipIds() throws Exception {
        mockMvc
            .perform(
                post("/api/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "title": "Invalid integration todo",
                          "description": "Missing relationship IDs."
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Validation error"))
            .andExpect(jsonPath("$.messages").isArray())
            .andExpect(jsonPath("$.messages[?(@ =~ /categoryId:.*/)]").exists())
            .andExpect(jsonPath("$.messages[?(@ =~ /folderId:.*/)]").exists());
    }

    @Test
    void shouldRejectTodoWithNonExistingRelationships() throws Exception {
        mockMvc
            .perform(
                post("/api/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "title": "Todo with invalid relationships",
                          "description": "References records that do not exist.",
                          "categoryId": "00000000-0000-0000-0000-000000000000",
                          "folderId": "11111111-1111-1111-1111-111111111111"
                        }
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Business rule error"))
            .andExpect(
                jsonPath("$.messages[0]").value(
                    "There is no category with the id 00000000-0000-0000-0000-000000000000"
                )
            );
    }
}
