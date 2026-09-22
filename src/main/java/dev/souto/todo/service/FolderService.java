package dev.souto.todo.service;

import dev.souto.todo.dto.FolderRequestDTO;
import dev.souto.todo.dto.FolderResponseDTO;
import dev.souto.todo.entity.FolderEntity;
import dev.souto.todo.exception.BusinessRulesException;
import dev.souto.todo.repository.FolderRepo;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class FolderService {

    private final FolderRepo folderRepo;

    public FolderService(FolderRepo folderRepo) {
        this.folderRepo = folderRepo;
    }

    public List<FolderResponseDTO> findAll() {
        List<FolderEntity> foldersList = folderRepo.findAll();

        List<FolderResponseDTO> foldersResponse = foldersList
            .stream()
            .map(folder -> FolderResponseDTO.toDto(folder))
            .toList();
        return foldersResponse;
    }

    public FolderResponseDTO findById(UUID id) {
        FolderEntity folderEntity = findOrThrow(id);

        FolderResponseDTO folderResponse = FolderResponseDTO.toDto(
            folderEntity
        );
        return folderResponse;
    }

    public FolderResponseDTO save(FolderRequestDTO dto) {
        FolderEntity folderEntity = FolderRequestDTO.toEntity(dto);

        if (folderRepo.findByName(dto.name()).isPresent()) {
            throw new BusinessRulesException(
                "There is already a folder with this name"
            );
        }

        FolderEntity savedFolder = folderRepo.save(folderEntity);
        return FolderResponseDTO.toDto(savedFolder);
    }

    @Transactional
    public FolderResponseDTO update(UUID id, FolderRequestDTO dto) {
        FolderEntity folderEntity = findOrThrow(id);
        FolderEntity newFolder = FolderRequestDTO.toEntity(dto);

        folderEntity.update(newFolder.getName());

        return FolderResponseDTO.toDto(folderEntity);
    }

    @Transactional
    public void delete(UUID id) {
        FolderEntity folderEntity = findOrThrow(id);

        if (folderEntity.getName().isEmpty()) {
            throw new BusinessRulesException(
                "Folder does not have a name, therefore cannot be deleted"
            );
        }

        folderRepo.deleteById(folderEntity.getId());
    }

    // Private methods
    private FolderEntity findOrThrow(UUID id) {
        return folderRepo
            .findById(id)
            .orElseThrow(() ->
                new BusinessRulesException(
                    "There is no folder found with this ID"
                )
            );
    }
}
