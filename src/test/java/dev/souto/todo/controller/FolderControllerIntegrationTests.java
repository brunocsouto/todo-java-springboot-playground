package dev.souto.todo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.souto.todo.entity.FolderEntity;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class FolderControllerIntegrationTests extends MongoIntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

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
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").value("Validation error"))
            .andExpect(
                jsonPath("$.messages[0]").value("name: Folder name is required")
            );
    }

    @Test
    void shouldRejectFolderWithWhitespaceOnlyName() throws Exception {
        mockMvc
            .perform(
                post("/api/folders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\": \"   \"}")
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.messages[0]").value("name: Folder name is required"));
    }

    @Test
    void shouldAcceptFolderNameAtMaximumLength() throws Exception {
        String name = "a".repeat(255);

        mockMvc
            .perform(
                post("/api/folders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\": \"%s\"}".formatted(name))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void shouldRejectFolderNameAboveMaximumLength() throws Exception {
        String name = "a".repeat(256);

        mockMvc
            .perform(
                post("/api/folders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\": \"%s\"}".formatted(name))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.messages[0]").value(
                "name: Folder name must have at most 255 characters"
            ));
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
        FolderEntity folder = persistFolder("Folder to delete " + UUID.randomUUID());

        mockMvc
            .perform(delete("/api/folders/{id}", folder.getId()))
            .andExpect(status().isNoContent());

        mockMvc
            .perform(get("/api/folders/{id}", folder.getId()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.detail").value("Resource not found"))
            .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    void shouldCascadeDeleteTodosWhenFolderIsDeleted() throws Exception {
        FolderEntity folder = persistFolder("Folder with todos " + UUID.randomUUID());
        var todo = persistTodo(
            "Todo in deleted folder " + UUID.randomUUID(),
            null,
            null,
            folder
        );

        mockMvc
            .perform(delete("/api/folders/{id}", folder.getId()))
            .andExpect(status().isNoContent());

        org.junit.jupiter.api.Assertions.assertFalse(
            todoRepo.existsById(todo.getId())
        );
    }

    @Test
    void shouldReturnNotFoundWhenFolderDoesNotExist() throws Exception {
        mockMvc
            .perform(get("/api/folders/{id}", UUID.randomUUID().toString()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.detail").value("Resource not found"))
            .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    void shouldReturnConflictWhenFolderNameAlreadyExists() throws Exception {
        String name = "Duplicate folder " + UUID.randomUUID();

        folderRepo.save(persistFolder(name));

        mockMvc
            .perform(
                post("/api/folders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {"name": "%s"}
                        """.formatted(name)
                    )
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.detail").value("Conflict"));
    }

    @Test
    void shouldRejectMalformedJson() throws Exception {
        mockMvc
            .perform(
                post("/api/folders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":")
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").value("Malformed JSON"))
            .andExpect(
                jsonPath("$.messages[0]").value("Request body is not valid JSON")
            );
    }

    @Test
    void shouldRejectInvalidFolderNameOnUpdate() throws Exception {
        FolderEntity folder = persistFolder("Folder to update " + UUID.randomUUID());

        mockMvc
            .perform(
                patch("/api/folders/{id}", folder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {"name": ""}
                        """
                    )
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").value("Validation error"))
            .andExpect(
                jsonPath("$.messages[0]").value("name: Folder name is required")
            );
    }

    @Test
    void shouldReturnConflictWhenUpdatingFolderToExistingName() throws Exception {
        FolderEntity original = persistFolder(
            "Original folder " + UUID.randomUUID()
        );
        String existingName = "Existing folder " + UUID.randomUUID();
        persistFolder(existingName);

        mockMvc
            .perform(
                patch("/api/folders/{id}", original.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {"name": "%s"}
                        """.formatted(existingName)
                    )
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.detail").value("Conflict"));
    }

    @Test
    void shouldReturnPaginationMetadata() throws Exception {
        persistFolder("Folder page " + UUID.randomUUID());

        mockMvc
            .perform(get("/api/folders?page=0&size=1&sort=name,asc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.number").value(0))
            .andExpect(jsonPath("$.size").value(1))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.totalElements").isNumber());
    }

    @Test
    void shouldReturnFoldersInAscendingNameOrder() throws Exception {
        folderRepo.deleteAll();
        String suffix = UUID.randomUUID().toString();
        String zebra = "Sort folder Z " + suffix;
        String alpha = "Sort folder A " + suffix;
        String middle = "Sort folder M " + suffix;

        persistFolder(zebra);
        persistFolder(alpha);
        persistFolder(middle);

        mockMvc
            .perform(
                get("/api/folders")
                    .param("page", "0")
                    .param("size", "3")
                    .param("sort", "name,asc")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value(alpha))
            .andExpect(jsonPath("$.content[1].name").value(middle))
            .andExpect(jsonPath("$.content[2].name").value(zebra));
    }

    @Test
    void shouldRejectUnsupportedFolderSortField() throws Exception {
        mockMvc
            .perform(get("/api/folders").param("sort", "description,asc"))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void shouldRejectUnsupportedFolderSortDirection() throws Exception {
        mockMvc
            .perform(get("/api/folders").param("sort", "name,sideways"))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void shouldRejectNegativeFolderPage() throws Exception {
        mockMvc
            .perform(get("/api/folders").param("page", "-1"))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void shouldRejectZeroFolderPageSize() throws Exception {
        mockMvc
            .perform(get("/api/folders").param("size", "0"))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void shouldRejectExcessiveFolderPageSize() throws Exception {
        mockMvc
            .perform(get("/api/folders").param("size", "101"))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.status").value(422));
    }
}
