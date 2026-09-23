package dev.souto.todo.service;

import dev.souto.todo.dto.FolderRequestDTO;
import dev.souto.todo.dto.FolderResponseDTO;
import dev.souto.todo.entity.FolderEntity;
import dev.souto.todo.exception.BusinessRulesException;
import dev.souto.todo.exception.ConflictException;
import dev.souto.todo.exception.ResourceNotFoundException;
import dev.souto.todo.repository.FolderRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FolderService {

    private final FolderRepo folderRepo;

    public FolderService(FolderRepo folderRepo) {
        this.folderRepo = folderRepo;
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
        return FolderResponseDTO.toDto(savedFolder);
    }

    public FolderResponseDTO update(String id, FolderRequestDTO dto) {
        FolderEntity folderEntity = findOrThrow(id);
        FolderEntity newFolder = FolderRequestDTO.toEntity(dto);

        folderEntity.update(newFolder.getName());
        FolderEntity savedFolder = folderRepo.save(folderEntity);

        return FolderResponseDTO.toDto(savedFolder);
    }

    public void delete(String id) {
        FolderEntity folderEntity = findOrThrow(id);

        if (folderEntity.getName().isEmpty()) {
            throw new BusinessRulesException(
                "Folder does not have a name, therefore cannot be deleted"
            );
        }

        folderRepo.deleteById(folderEntity.getId());
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
