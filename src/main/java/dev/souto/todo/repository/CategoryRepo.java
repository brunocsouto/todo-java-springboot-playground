package dev.souto.todo.repository;

import dev.souto.todo.entity.CategoryEntity;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepo extends MongoRepository<CategoryEntity, String> {
    Optional<CategoryEntity> findByName(String name);
}
