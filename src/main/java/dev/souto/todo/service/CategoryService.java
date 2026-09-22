package dev.souto.todo.service;

import dev.souto.todo.dto.CategoryRequestDTO;
import dev.souto.todo.dto.CategoryResponseDTO;
import dev.souto.todo.entity.CategoryEntity;
import dev.souto.todo.exception.BusinessRulesException;
import dev.souto.todo.repository.CategoryRepo;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryRepo categoryRepo;

    public CategoryService(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public List<CategoryResponseDTO> findAll() {
        List<CategoryEntity> categoriesList = categoryRepo.findAll();

        List<CategoryResponseDTO> responseList = categoriesList
            .stream()
            .map(category -> CategoryResponseDTO.toDto(category))
            .toList();
        return responseList;
    }

    public CategoryResponseDTO findById(UUID id) {
        CategoryEntity categoryEntity = findOrThrow(id);
        return CategoryResponseDTO.toDto(categoryEntity);
    }

    public CategoryResponseDTO findByName(String name) {
        CategoryEntity categoryEntity = categoryRepo
            .findByName(name)
            .orElseThrow(() ->
                new BusinessRulesException(
                    "There is no category found with this name."
                )
            );

        return CategoryResponseDTO.toDto(categoryEntity);
    }

    public CategoryResponseDTO save(CategoryRequestDTO dto) {
        CategoryEntity categoryEntity = CategoryRequestDTO.toEntity(dto);

        CategoryEntity existingEntity = categoryRepo
            .findByName(categoryEntity.getName())
            .orElse(null);

        if (existingEntity != null) {
            throw new BusinessRulesException(
                "Category with this name already exists."
            );
        }

        CategoryEntity savedEntity = categoryRepo.save(categoryEntity);
        return CategoryResponseDTO.toDto(savedEntity);
    }

    @Transactional
    public CategoryResponseDTO update(UUID id, CategoryRequestDTO dto) {
        CategoryEntity categoryEntity = CategoryRequestDTO.toEntity(dto);

        CategoryEntity foundEntity = findOrThrow(id);
        foundEntity.update(categoryEntity.getName());

        return CategoryResponseDTO.toDto(foundEntity);
    }

    public void delete(UUID id) {
        CategoryEntity categoryEntity = findOrThrow(id);
        categoryRepo.deleteById(categoryEntity.getId());
    }

    // Private methods
    private CategoryEntity findOrThrow(UUID id) {
        CategoryEntity categoryEntity = categoryRepo
            .findById(id)
            .orElseThrow(() ->
                new BusinessRulesException(
                    "There is no category found with this ID."
                )
            );
        return categoryEntity;
    }
}
