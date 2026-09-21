package dev.souto.todo.entity;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "folders")
@Getter
public class FolderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "folder")
    private List<TodoEntity> todos;

    protected FolderEntity() {
    }

    public FolderEntity(String name) {
        this.name = name;
    }

    public FolderEntity update(String name) {
        this.name = name;
        return this;
    }
}
