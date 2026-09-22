package dev.souto.todo.controller;

import dev.souto.todo.dto.CategoryRequestDTO;
import dev.souto.todo.dto.CategoryResponseDTO;
import dev.souto.todo.pagination.PageResponse;
import dev.souto.todo.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(params = { "page", "size" })
    public PageResponse<CategoryResponseDTO> findAllCategories(
        @RequestParam @PositiveOrZero int page,
        @RequestParam @Positive int size
    ) {
        PageResponse<CategoryResponseDTO> categories = categoryService.findAll(
            page,
            size
        );
        return categories;
    }

    @GetMapping
    public PageResponse<CategoryResponseDTO> findAllCategories() {
        PageResponse<CategoryResponseDTO> categories =
            categoryService.findAll();
        return categories;
    }

    @GetMapping("/{id}")
    public CategoryResponseDTO findCategoryById(@PathVariable @Valid String id) {
        CategoryResponseDTO category = categoryService.findById(id);
        return category;
    }

    @PostMapping
    public CategoryResponseDTO createCategory(
        @RequestBody @Valid CategoryRequestDTO dto
    ) {
        CategoryResponseDTO category = categoryService.save(dto);
        return category;
    }

    @PatchMapping("/{id}")
    public CategoryResponseDTO updateCategory(
        @PathVariable @Valid String id,
        @RequestBody @Valid CategoryRequestDTO dto
    ) {
        CategoryResponseDTO category = categoryService.update(id, dto);
        return category;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable @Valid String id) {
        categoryService.delete(id);
    }
}
