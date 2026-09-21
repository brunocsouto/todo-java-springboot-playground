package dev.souto.todo.service;

import dev.souto.todo.entity.CategoryEntity;
import dev.souto.todo.dto.CategoryRequestDTO;
import dev.souto.todo.dto.CategoryResponseDTO;
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
        List<CategoryEntity> entity = categoryRepo.findAll();

        List<CategoryResponseDTO> responseList = entity
            .stream()
            .map(category -> CategoryResponseDTO.toDto(category))
            .toList();
        return responseList;
    }

    public CategoryResponseDTO findById(UUID id) {
        CategoryEntity entity = findOrThrow(id);
        return CategoryResponseDTO.toDto(entity);
    }

    public CategoryResponseDTO findByName(String name) {
        CategoryEntity categoryEntity = categoryRepo
            .findByName(name)
            .orElseThrow(() ->
                new RuntimeException(
                    "There is no category found with this name."
                )
            );

        return CategoryResponseDTO.toDto(categoryEntity);
    }

    public CategoryResponseDTO save(CategoryRequestDTO dto) {
        CategoryEntity categoryEntity = CategoryRequestDTO.toEntity(dto);

        if(categoryRepo.findByName(categoryEntity.getName()).isPresent()) {
            throw new RuntimeException("There is already a category found with this name.");
        }
        CategoryEntity savedEntity = categoryRepo.save(categoryEntity);
        return CategoryResponseDTO.toDto(savedEntity);
    }

    @Transactional
    public CategoryResponseDTO update(UUID id, CategoryRequestDTO dto) {
        CategoryEntity entity = CategoryRequestDTO.toEntity(dto);

        CategoryEntity foundEntity = findOrThrow(id);
        foundEntity.setName(entity.getName());

        return CategoryResponseDTO.toDto(foundEntity);
    }

    public void delete(UUID id) {
        CategoryEntity entity = findOrThrow(id);
        categoryRepo.deleteById(entity.getId());
    }

    // Private methods
    private CategoryEntity findOrThrow(UUID id) {
        CategoryEntity entity = categoryRepo
            .findById(id)
            .orElseThrow(() ->
                new RuntimeException("There is no category found with this ID.")
            );
        return entity;
    }
}
