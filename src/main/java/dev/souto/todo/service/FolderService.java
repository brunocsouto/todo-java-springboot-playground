package dev.souto.todo.service;

import dev.souto.todo.entity.FolderEntity;
import dev.souto.todo.dto.FolderRequestDTO;
import dev.souto.todo.dto.FolderResponseDTO;
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
        List<FolderEntity> folders = folderRepo.findAll();

        List<FolderResponseDTO> foldersResponse = folders
            .stream()
            .map(folder -> FolderResponseDTO.toDto(folder))
            .toList();
        return foldersResponse;
    }

    public FolderResponseDTO findById(UUID id) {
        FolderEntity foundFolder = findOrThrow(id);

        FolderResponseDTO folderResponse = FolderResponseDTO.toDto(foundFolder);
        return folderResponse;
    }

    @Transactional
    public FolderResponseDTO save(FolderRequestDTO dto) {
        FolderEntity entity = FolderRequestDTO.toEntity(dto);

        if(folderRepo.findByName(dto.name()).isPresent()) {
            throw new RuntimeException("There is already a folder with this name");
        }

        FolderEntity savedFolder = folderRepo.save(entity);
        savedFolder.setName(entity.getName());
        return FolderResponseDTO.toDto(savedFolder);
    }

    @Transactional
    public FolderResponseDTO update(UUID id, FolderRequestDTO dto) {
        FolderEntity foundFolder = findOrThrow(id);
        FolderEntity newFolder = FolderRequestDTO.toEntity(dto);

        foundFolder.setName(newFolder.getName());

        return FolderResponseDTO.toDto(foundFolder);
    }

    @Transactional
    public void delete(UUID id) {
        FolderEntity folder = findOrThrow(id);

        if (folder.getName().isEmpty()) {
            throw new RuntimeException(
                "Folder does not have a name, therefore cannot be deleted"
            );
        }

        folderRepo.deleteById(folder.getId());
    }

    // Private methods
    private FolderEntity findOrThrow(UUID id) {
        return folderRepo
            .findById(id)
            .orElseThrow(() ->
                new RuntimeException("There is no folder found with this ID")
            );
    }
}
