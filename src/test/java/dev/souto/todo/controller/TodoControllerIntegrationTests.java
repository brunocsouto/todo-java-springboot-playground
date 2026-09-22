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

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateTodoWithExistingRelationships() throws Exception {
        mockMvc
            .perform(
                post("/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "title": "Integration todo",
                          "description": "Created by an integration test.",
                          "categoryId": "5aa71afa-8678-40ed-bb57-c6341046d165",
                          "folderId": "5a4d155f-616a-49b4-9a31-816b8d925cdc"
                        }
                        """)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Integration todo"))
            .andExpect(jsonPath("$.category.name").value("Development"))
            .andExpect(jsonPath("$.folder.name").value("Work"));
    }

    @Test
    void shouldRejectTodoWithoutRelationshipIds() throws Exception {
        mockMvc
            .perform(
                post("/todos")
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
                post("/todos")
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
            .andExpect(jsonPath("$.error").value("Business rules error"))
            .andExpect(
                jsonPath("$.messages[0]").value(
                    "There is no category with the id 00000000-0000-0000-0000-000000000000"
                )
            );
    }
}
