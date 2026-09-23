package dev.souto.todo.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "folders")
public class FolderEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    protected FolderEntity() {}

    public FolderEntity(String name) {
        this.name = name;
    }

    public FolderEntity update(String name) {
        this.name = name;
        return this;
    }
}
