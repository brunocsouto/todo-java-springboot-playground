package dev.souto.todo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.souto.todo.dto.CategoryRequestDTO;
import dev.souto.todo.entity.CategoryEntity;
import dev.souto.todo.exception.ConflictException;
import dev.souto.todo.exception.ResourceNotFoundException;
import dev.souto.todo.repository.CategoryRepo;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceUnitTests {

    @Mock
    private CategoryRepo categoryRepo;

    private CategoryService service;

    @BeforeEach
    void setUp() {
        service = new CategoryService(categoryRepo);
    }

    @Test
    void shouldCreateCategory() {
        when(categoryRepo.findByName("Development")).thenReturn(Optional.empty());
        when(categoryRepo.save(any(CategoryEntity.class))).thenAnswer(invocation ->
            invocation.getArgument(0)
        );

        var response = service.save(new CategoryRequestDTO("Development"));

        assertEquals("Development", response.name());
        verify(categoryRepo).save(any(CategoryEntity.class));
    }

    @Test
    void shouldRejectDuplicateCategoryName() {
        when(categoryRepo.findByName("Development")).thenReturn(
            Optional.of(new CategoryEntity("Development"))
        );

        assertThrows(
            ConflictException.class,
            () -> service.save(new CategoryRequestDTO("Development"))
        );
    }

    @Test
    void shouldUpdateCategory() {
        CategoryEntity category = new CategoryEntity("Old name");
        when(categoryRepo.findById("category-id")).thenReturn(
            Optional.of(category)
        );
        when(
            categoryRepo.findByNameAndIdNot("New name", "category-id")
        ).thenReturn(Optional.empty());
        when(categoryRepo.save(category)).thenReturn(category);

        var response = service.update(
            "category-id",
            new CategoryRequestDTO("New name")
        );

        assertEquals("New name", response.name());
        verify(categoryRepo).save(category);
    }

    @Test
    void shouldRejectUpdateToDuplicateCategoryName() {
        CategoryEntity category = new CategoryEntity("Old name");
        when(categoryRepo.findById("category-id")).thenReturn(
            Optional.of(category)
        );
        when(
            categoryRepo.findByNameAndIdNot("Development", "category-id")
        ).thenReturn(Optional.of(new CategoryEntity("Development")));

        assertThrows(
            ConflictException.class,
            () ->
                service.update(
                    "category-id",
                    new CategoryRequestDTO("Development")
                )
        );
    }

    @Test
    void shouldRejectUpdateWhenCategoryDoesNotExist() {
        when(categoryRepo.findById("missing-category")).thenReturn(
            Optional.empty()
        );

        assertThrows(
            ResourceNotFoundException.class,
            () ->
                service.update(
                    "missing-category",
                    new CategoryRequestDTO("Development")
                )
        );
    }

    @Test
    void shouldDeleteExistingCategory() {
        CategoryEntity category = new CategoryEntity("Development");
        when(categoryRepo.findById("category-id")).thenReturn(
            Optional.of(category)
        );

        service.delete("category-id");

        verify(categoryRepo).deleteById(category.getId());
    }

    @Test
    void shouldRejectDeleteWhenCategoryDoesNotExist() {
        when(categoryRepo.findById("missing-category")).thenReturn(
            Optional.empty()
        );

        assertThrows(
            ResourceNotFoundException.class,
            () -> service.delete("missing-category")
        );
    }
}
