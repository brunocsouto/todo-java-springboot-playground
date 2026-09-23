package dev.souto.todo.service;

import dev.souto.todo.dto.FolderRequestDTO;
import dev.souto.todo.dto.FolderResponseDTO;
import dev.souto.todo.entity.FolderEntity;
import dev.souto.todo.exception.BusinessRulesException;
import dev.souto.todo.exception.ConflictException;
import dev.souto.todo.exception.ResourceNotFoundException;
import dev.souto.todo.repository.FolderRepo;
import dev.souto.todo.repository.TodoRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FolderService {

    private static final Logger logger = LoggerFactory.getLogger(
        FolderService.class
    );
    private final FolderRepo folderRepo;
    private final TodoRepo todoRepo;

    public FolderService(FolderRepo folderRepo, TodoRepo todoRepo) {
        this.folderRepo = folderRepo;
        this.todoRepo = todoRepo;
    }

    public Page<FolderResponseDTO> findAll(Pageable pageable) {
        Page<FolderEntity> foldersList = folderRepo.findAll(pageable);

        Page<FolderResponseDTO> foldersResponse = foldersList.map(
            FolderResponseDTO::toDto
        );
        return foldersResponse;
    }

    public FolderResponseDTO findById(String id) {
        FolderEntity folderEntity = findOrThrow(id);

        FolderResponseDTO folderResponse = FolderResponseDTO.toDto(
            folderEntity
        );
        return folderResponse;
    }

    public FolderResponseDTO save(FolderRequestDTO dto) {
        FolderEntity folderEntity = FolderRequestDTO.toEntity(dto);

        if (folderRepo.findByName(dto.name()).isPresent()) {
            throw new ConflictException(
                "There is already a folder with this name"
            );
        }

        FolderEntity savedFolder = folderRepo.save(folderEntity);
        logger.atInfo()
            .addKeyValue("folderId", savedFolder.getId())
            .log("Folder created");
        return FolderResponseDTO.toDto(savedFolder);
    }

    public FolderResponseDTO update(String id, FolderRequestDTO dto) {
        FolderEntity folderEntity = findOrThrow(id);
        FolderEntity newFolder = FolderRequestDTO.toEntity(dto);

        if (folderRepo.findByNameAndIdNot(dto.name(), id).isPresent()) {
            throw new ConflictException(
                "There is already a folder with this name"
            );
        }

        folderEntity.update(newFolder.getName());
        FolderEntity savedFolder = folderRepo.save(folderEntity);

        logger.atInfo()
            .addKeyValue("folderId", savedFolder.getId())
            .log("Folder updated");
        return FolderResponseDTO.toDto(savedFolder);
    }

    public void delete(String id) {
        FolderEntity folderEntity = findOrThrow(id);

        if (folderEntity.getName().isEmpty()) {
            throw new BusinessRulesException(
                "Folder does not have a name, therefore cannot be deleted"
            );
        }

        todoRepo.deleteByFolder(folderEntity);
        folderRepo.deleteById(folderEntity.getId());
        logger.atInfo()
            .addKeyValue("folderId", folderEntity.getId())
            .log("Folder deleted");
    }

    // Private methods
    private FolderEntity findOrThrow(String id) {
        return folderRepo
            .findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "There is no folder found with this ID"
                )
            );
    }
}
