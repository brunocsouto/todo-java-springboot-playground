package dev.souto.todo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.souto.todo.entity.FolderEntity;
import dev.souto.todo.repository.FolderRepo;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class FolderControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FolderRepo folderRepo;

    @Test
    void shouldRejectFolderWithoutName() throws Exception {
        mockMvc
            .perform(
                post("/api/folders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {"name": ""}
                        """
                    )
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Validation error"))
            .andExpect(
                jsonPath("$.messages[0]").value("name: Folder name is required")
            );
    }

    @Test
    void shouldCreateFolderWithValidName() throws Exception {
        String folderName = "Integration Folder " + UUID.randomUUID();

        mockMvc
            .perform(
                post("/api/folders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {"name": "%s"}
                        """.formatted(folderName)
                    )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(folderName));
    }

    @Test
    void shouldDeleteFolderAndReturnNoContent() throws Exception {
        FolderEntity folder = folderRepo.save(
            new FolderEntity("Folder to delete " + UUID.randomUUID())
        );

        mockMvc
            .perform(delete("/api/folders/{id}", folder.getId()))
            .andExpect(status().isNoContent());

        mockMvc
            .perform(get("/api/folders/{id}", folder.getId()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Resource not found"))
            .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    void shouldReturnNotFoundWhenFolderDoesNotExist() throws Exception {
        mockMvc
            .perform(get("/api/folders/{id}", UUID.randomUUID().toString()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Resource not found"))
            .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    void shouldReturnConflictWhenFolderNameAlreadyExists() throws Exception {
        String name = "Duplicate folder " + UUID.randomUUID();

        folderRepo.save(new FolderEntity(name));

        mockMvc.perform(
            post("/api/folders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "%s"}
                    """.formatted(name))
        )
        .andExpect(status().isUnprocessableContent())
        .andExpect(jsonPath("$.status").value(422))
        .andExpect(jsonPath("$.error").value("Business rule error"));
    }
}
