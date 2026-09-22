package dev.souto.todo.repository;

import dev.souto.todo.entity.FolderEntity;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolderRepo extends MongoRepository<FolderEntity, String> {
    Optional<FolderEntity> findByName(String name);
}
