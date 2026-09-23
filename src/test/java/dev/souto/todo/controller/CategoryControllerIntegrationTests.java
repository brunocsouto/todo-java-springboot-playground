package dev.souto.todo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.souto.todo.entity.CategoryEntity;
import dev.souto.todo.repository.CategoryRepo;
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
class CategoryControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepo categoryRepo;

    private CategoryEntity persistCategory(String name) throws Exception {
        CategoryEntity category = new CategoryEntity(name);
        Field field = CategoryEntity.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(category, UUID.randomUUID().toString());
        return categoryRepo.save(category);
    }

    @Test
    void shouldRejectCategoryWithoutName() throws Exception {
        mockMvc
            .perform(
                post("/api/categories")
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
                    .content(
                        """
                        {"name": "%s"}
                        """.formatted(categoryName)
                    )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(categoryName));
    }

    @Test
    void shouldDeleteCategoryAndReturnNoContent() throws Exception {
        CategoryEntity category = persistCategory(
            "Category to delete " + UUID.randomUUID()
        );

        mockMvc
            .perform(delete("/api/categories/{id}", category.getId()))
            .andExpect(status().isNoContent());

        mockMvc
            .perform(get("/api/categories/{id}", category.getId()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.detail").value("Resource not found"))
            .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    void shouldReturnConflictWhenCategoryNameAlreadyExists() throws Exception {
        String name = "Duplicate category " + UUID.randomUUID();
        categoryRepo.save(persistCategory(name));

        mockMvc
            .perform(
                post("/api/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {"name": "%s"}
                        """.formatted(name)
                    )
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.detail").value("Conflict"))
            .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    void shouldRejectInvalidCategoryNameOnUpdate() throws Exception {
        CategoryEntity category = persistCategory(
            "Category to update " + UUID.randomUUID()
        );

        mockMvc
            .perform(
                patch("/api/categories/{id}", category.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {"name": ""}
                        """
                    )
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").value("Validation error"));
    }

    @Test
    void shouldReturnPagedCategories() throws Exception {
        persistCategory("Category page " + UUID.randomUUID());

        mockMvc
            .perform(get("/api/categories?page=0&size=1&sort=name,asc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.number").value(0))
            .andExpect(jsonPath("$.size").value(1))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.totalElements").isNumber());
    }
}
