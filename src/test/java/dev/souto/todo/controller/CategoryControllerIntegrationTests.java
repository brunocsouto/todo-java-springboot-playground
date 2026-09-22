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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import dev.souto.todo.entity.CategoryEntity;
import dev.souto.todo.repository.CategoryRepo;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepo categoryRepo;

    @Test
    void shouldRejectCategoryWithoutName() throws Exception {
        mockMvc
            .perform(
                post("/api/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"name": ""}
                        """)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Validation error"))
            .andExpect(
                jsonPath("$.messages[0]").value(
                    "name: Category name is required"
                )
            );
    }

    @Test
    void shouldCreateCategoryWithValidName() throws Exception {
        String categoryName = "Integration Category " + UUID.randomUUID();

        mockMvc
            .perform(
                post("/api/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {"name": "%s"}
                        """.formatted(categoryName))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(categoryName));
    }

    @Test
    void shouldDeleteCategoryAndReturnNoContent() throws Exception {
        CategoryEntity category = categoryRepo.save(
            new CategoryEntity("Category to delete " + UUID.randomUUID())
        );

        mockMvc
            .perform(delete("/api/categories/{id}", category.getId()))
            .andExpect(status().isNoContent());

        mockMvc
            .perform(get("/api/categories/{id}", category.getId()))
            .andExpect(status().isBadRequest());
    }
}
