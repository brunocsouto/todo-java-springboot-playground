package dev.souto.todo.service;

import dev.souto.todo.dto.CategoryRequestDTO;
import dev.souto.todo.dto.CategoryResponseDTO;
import dev.souto.todo.entity.CategoryEntity;
import dev.souto.todo.exception.ConflictException;
import dev.souto.todo.exception.ResourceNotFoundException;
import dev.souto.todo.repository.CategoryRepo;
import dev.souto.todo.repository.TodoRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(
        CategoryService.class
    );
    private final CategoryRepo categoryRepo;
    private final TodoRepo todoRepo;

    public CategoryService(CategoryRepo categoryRepo, TodoRepo todoRepo) {
        this.categoryRepo = categoryRepo;
        this.todoRepo = todoRepo;
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
        logger.atInfo()
            .addKeyValue("categoryId", savedEntity.getId())
            .log("Category created");
        return CategoryResponseDTO.toDto(savedEntity);
    }

    public CategoryResponseDTO update(String id, CategoryRequestDTO dto) {
        CategoryEntity categoryEntity = CategoryRequestDTO.toEntity(dto);

        CategoryEntity foundEntity = findOrThrow(id);
        if (categoryRepo.findByNameAndIdNot(dto.name(), id).isPresent()) {
            throw new ConflictException(
                "Category with this name already exists."
            );
        }

        foundEntity.update(categoryEntity.getName());
        CategoryEntity savedEntity = categoryRepo.save(foundEntity);

        logger.atInfo()
            .addKeyValue("categoryId", savedEntity.getId())
            .log("Category updated");
        return CategoryResponseDTO.toDto(savedEntity);
    }

    public void delete(String id) {
        CategoryEntity categoryEntity = findOrThrow(id);
        todoRepo.deleteByCategory(categoryEntity);
        categoryRepo.deleteById(categoryEntity.getId());
        logger.atInfo()
            .addKeyValue("categoryId", categoryEntity.getId())
            .log("Category deleted");
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
