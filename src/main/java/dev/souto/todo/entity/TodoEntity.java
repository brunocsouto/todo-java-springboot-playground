package dev.souto.todo.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "todos")
public class TodoEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String title;

    private String description;

    @DBRef
    private FolderEntity folder;

    @DBRef
    private CategoryEntity category;

    protected TodoEntity() {}

    public TodoEntity(
        String title,
        String description,
        CategoryEntity category,
        FolderEntity folder
    ) {
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
