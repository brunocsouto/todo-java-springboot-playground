package dev.souto.todo.service;

import dev.souto.todo.dto.CategoryRequestDTO;
import dev.souto.todo.dto.CategoryResponseDTO;
import dev.souto.todo.entity.CategoryEntity;
import dev.souto.todo.exception.ConflictException;
import dev.souto.todo.exception.ResourceNotFoundException;
import dev.souto.todo.repository.CategoryRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepo categoryRepo;

    public CategoryService(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public Page<CategoryResponseDTO> findAll(Pageable pageable) {
        Page<CategoryEntity> categoriesList = categoryRepo.findAll(pageable);

        Page<CategoryResponseDTO> responseList = categoriesList.map(
            CategoryResponseDTO::toDto
        );

        return responseList;
    }

    public CategoryResponseDTO findById(String id) {
        CategoryEntity categoryEntity = findOrThrow(id);
        return CategoryResponseDTO.toDto(categoryEntity);
    }

    public CategoryResponseDTO save(CategoryRequestDTO dto) {
        CategoryEntity categoryEntity = CategoryRequestDTO.toEntity(dto);

        CategoryEntity existingEntity = categoryRepo
            .findByName(categoryEntity.getName())
            .orElse(null);

        if (existingEntity != null) {
            throw new ConflictException(
                "Category with this name already exists."
            );
        }

        CategoryEntity savedEntity = categoryRepo.save(categoryEntity);
        return CategoryResponseDTO.toDto(savedEntity);
    }

    public CategoryResponseDTO update(String id, CategoryRequestDTO dto) {
        CategoryEntity categoryEntity = CategoryRequestDTO.toEntity(dto);

        CategoryEntity foundEntity = findOrThrow(id);
        foundEntity.update(categoryEntity.getName());
        CategoryEntity savedEntity = categoryRepo.save(foundEntity);

        return CategoryResponseDTO.toDto(savedEntity);
    }

    public void delete(String id) {
        CategoryEntity categoryEntity = findOrThrow(id);
        categoryRepo.deleteById(categoryEntity.getId());
    }

    // Private methods
    private CategoryEntity findOrThrow(String id) {
        CategoryEntity categoryEntity = categoryRepo
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "There is no category found with this ID."
                )
            );
        return categoryEntity;
    }
}
