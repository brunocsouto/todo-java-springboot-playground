package dev.souto.todo.controller;

import dev.souto.todo.dto.FolderRequestDTO;
import dev.souto.todo.dto.FolderResponseDTO;
import dev.souto.todo.service.FolderService;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("api/folders")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @GetMapping
    public Page<FolderResponseDTO> listAllFolders(
        @PageableDefault Pageable pageable
    ) {
        return folderService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public FolderResponseDTO findFolderById(@PathVariable String id) {
        FolderResponseDTO dto = folderService.findById(id);
        return dto;
    }

    @PostMapping
    public FolderResponseDTO createFolder(
        @RequestBody @Valid FolderRequestDTO dto
    ) {
        FolderResponseDTO responseDto = folderService.save(dto);
        return responseDto;
    }

    @PatchMapping("/{id}")
    public FolderResponseDTO updateFolder(
        @PathVariable String id,
        @RequestBody @Valid FolderRequestDTO dto
    ) {
        FolderResponseDTO responseDTO = folderService.update(id, dto);
        return responseDTO;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFolder(@PathVariable String id) {
        folderService.delete(id);
    }
}
