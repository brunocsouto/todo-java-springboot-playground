package dev.souto.todo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.souto.todo.dto.FolderRequestDTO;
import dev.souto.todo.entity.FolderEntity;
import dev.souto.todo.exception.ConflictException;
import dev.souto.todo.exception.ResourceNotFoundException;
import dev.souto.todo.repository.FolderRepo;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FolderServiceUnitTests {

    @Mock
    private FolderRepo folderRepo;

    private FolderService service;

    @BeforeEach
    void setUp() {
        service = new FolderService(folderRepo);
    }

    @Test
    void shouldCreateFolder() {
        when(folderRepo.findByName("Work")).thenReturn(Optional.empty());
        when(folderRepo.save(any(FolderEntity.class))).thenAnswer(invocation ->
            invocation.getArgument(0)
        );

        var response = service.save(new FolderRequestDTO("Work"));

        assertEquals("Work", response.name());
        verify(folderRepo).save(any(FolderEntity.class));
    }

    @Test
    void shouldRejectDuplicateFolderName() {
        when(folderRepo.findByName("Work")).thenReturn(
            Optional.of(new FolderEntity("Work"))
        );

        assertThrows(
            ConflictException.class,
            () -> service.save(new FolderRequestDTO("Work"))
        );
    }

    @Test
    void shouldUpdateFolder() {
        FolderEntity folder = new FolderEntity("Old name");
        when(folderRepo.findById("folder-id")).thenReturn(Optional.of(folder));
        when(folderRepo.findByNameAndIdNot("New name", "folder-id")).thenReturn(
            Optional.empty()
        );
        when(folderRepo.save(folder)).thenReturn(folder);

        var response = service.update(
            "folder-id",
            new FolderRequestDTO("New name")
        );

        assertEquals("New name", response.name());
        verify(folderRepo).save(folder);
    }

    @Test
    void shouldRejectUpdateToDuplicateFolderName() {
        FolderEntity folder = new FolderEntity("Old name");
        when(folderRepo.findById("folder-id")).thenReturn(Optional.of(folder));
        when(folderRepo.findByNameAndIdNot("Work", "folder-id")).thenReturn(
            Optional.of(new FolderEntity("Work"))
        );

        assertThrows(
            ConflictException.class,
            () -> service.update("folder-id", new FolderRequestDTO("Work"))
        );
    }

    @Test
    void shouldRejectUpdateWhenFolderDoesNotExist() {
        when(folderRepo.findById("missing-folder")).thenReturn(Optional.empty());

        assertThrows(
            ResourceNotFoundException.class,
            () -> service.update("missing-folder", new FolderRequestDTO("Work"))
        );
    }

    @Test
    void shouldDeleteExistingFolder() {
        FolderEntity folder = new FolderEntity("Work");
        when(folderRepo.findById("folder-id")).thenReturn(Optional.of(folder));

        service.delete("folder-id");

        verify(folderRepo).deleteById(folder.getId());
    }

    @Test
    void shouldRejectDeleteWhenFolderDoesNotExist() {
        when(folderRepo.findById("missing-folder")).thenReturn(Optional.empty());

        assertThrows(
            ResourceNotFoundException.class,
            () -> service.delete("missing-folder")
        );
    }
}
