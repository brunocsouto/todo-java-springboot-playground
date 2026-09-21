package dev.souto.todo.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.souto.todo.entity.FolderEntity;

@Repository
public interface FolderRepo extends JpaRepository<FolderEntity, UUID> {
    Optional<FolderEntity> findByName(String name);
}
