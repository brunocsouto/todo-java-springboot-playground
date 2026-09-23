package dev.souto.todo.controller;

import dev.souto.todo.dto.CategoryRequestDTO;
import dev.souto.todo.dto.CategoryResponseDTO;
import dev.souto.todo.pagination.SortValidator;
import dev.souto.todo.pagination.PaginationValidator;
import dev.souto.todo.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public Page<CategoryResponseDTO> findAllCategories(
        @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
        HttpServletRequest request

    ) {
        SortValidator.validate(request.getParameter("sort"), Set.of("name"));
        PaginationValidator.validate(
            request.getParameter("page"),
            request.getParameter("size")
        );
        return categoryService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public CategoryResponseDTO findCategoryById(@PathVariable String id) {
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
        @PathVariable String id,
        @RequestBody @Valid CategoryRequestDTO dto
    ) {
        CategoryResponseDTO category = categoryService.update(id, dto);
        return category;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable String id) {
        categoryService.delete(id);
    }
}
