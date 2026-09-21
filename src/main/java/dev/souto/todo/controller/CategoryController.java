package dev.souto.todo.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dev.souto.todo.dto.CategoryRequestDTO;
import dev.souto.todo.dto.CategoryResponseDTO;
import dev.souto.todo.service.CategoryService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponseDTO> findAllCategories() {
         List<CategoryResponseDTO> categories = categoryService.findAll();
         return categories;
    }

    @GetMapping("/{id}")
    public CategoryResponseDTO findCategoryById(@PathVariable UUID id) {
        CategoryResponseDTO category = categoryService.findById(id);
        return category;
    }

    @PostMapping
    public CategoryResponseDTO createCategory(@Valid @RequestBody CategoryRequestDTO dto) {
        CategoryResponseDTO category = categoryService.save(dto);
        return category;
    }

    @PatchMapping("/{id}")
    public CategoryResponseDTO updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoryRequestDTO dto) {
        CategoryResponseDTO category = categoryService.update(id, dto);
        return category;
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable UUID id) {
        categoryService.delete(id);
    }
}
