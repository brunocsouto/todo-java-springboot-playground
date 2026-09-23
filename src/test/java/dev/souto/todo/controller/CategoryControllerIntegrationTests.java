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
    void shouldRejectCategoryWithWhitespaceOnlyName() throws Exception {
        mockMvc
            .perform(
                post("/api/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\": \"   \"}")
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.messages[0]").value("name: Category name is required"));
    }

    @Test
    void shouldAcceptCategoryNameAtMaximumLength() throws Exception {
        String name = "a".repeat(255);

        mockMvc
            .perform(
                post("/api/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\": \"%s\"}".formatted(name))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void shouldRejectCategoryNameAboveMaximumLength() throws Exception {
        String name = "a".repeat(256);

        mockMvc
            .perform(
                post("/api/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\": \"%s\"}".formatted(name))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.messages[0]").value(
                "name: Category name must have at most 255 characters"
            ));
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
    void shouldReturnConflictWhenUpdatingCategoryToExistingName() throws Exception {
        CategoryEntity original = persistCategory(
            "Original category " + UUID.randomUUID()
        );
        String existingName = "Existing category " + UUID.randomUUID();
        persistCategory(existingName);

        mockMvc
            .perform(
                patch("/api/categories/{id}", original.getId())
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

    @Test
    void shouldReturnCategoriesInDescendingNameOrder() throws Exception {
        categoryRepo.deleteAll();
        String suffix = UUID.randomUUID().toString();
        String zebra = "Sort category Z " + suffix;
        String alpha = "Sort category A " + suffix;
        String middle = "Sort category M " + suffix;

        persistCategory(zebra);
        persistCategory(alpha);
        persistCategory(middle);

        mockMvc
            .perform(
                get("/api/categories")
                    .param("page", "0")
                    .param("size", "3")
                    .param("sort", "name,desc")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value(zebra))
            .andExpect(jsonPath("$.content[1].name").value(middle))
            .andExpect(jsonPath("$.content[2].name").value(alpha));
    }

    @Test
    void shouldRejectUnsupportedCategorySortField() throws Exception {
        mockMvc
            .perform(get("/api/categories").param("sort", "description,asc"))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void shouldRejectUnsupportedCategorySortDirection() throws Exception {
        mockMvc
            .perform(get("/api/categories").param("sort", "name,sideways"))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void shouldRejectNegativeCategoryPage() throws Exception {
        mockMvc
            .perform(get("/api/categories").param("page", "-1"))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void shouldRejectZeroCategoryPageSize() throws Exception {
        mockMvc
            .perform(get("/api/categories").param("size", "0"))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void shouldRejectExcessiveCategoryPageSize() throws Exception {
        mockMvc
            .perform(get("/api/categories").param("size", "101"))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.status").value(422));
    }
}
