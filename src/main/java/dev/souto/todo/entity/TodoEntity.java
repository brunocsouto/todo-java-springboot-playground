package dev.souto.todo.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "todos")
@Getter
public class TodoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "description")
    private String description;

    @JoinColumn(name = "folder_id")
    @ManyToOne
    private FolderEntity folder;

    @JoinColumn(name = "category_id")
    @ManyToOne
    private CategoryEntity category;

    protected TodoEntity() {
    }

    public TodoEntity(String title, String description, CategoryEntity category, FolderEntity folder) {
        this.title = title;
        this.description = description;
        bindCategory(category);
        bindFolder(folder);
    }

    public TodoEntity update(String title, String description) {
        this.title = title;
        this.description = description;

        return this;
    }

    public void bindCategory(CategoryEntity category) {
        this.category = category;
    }

    public void bindFolder(FolderEntity folder) {
        this.folder = folder;
    }
}
