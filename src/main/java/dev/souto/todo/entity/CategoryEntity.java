package dev.souto.todo.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "categories")
public class CategoryEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    protected CategoryEntity() {}

    public CategoryEntity(String name) {
        this.name = name;
    }

    public CategoryEntity update(String name) {
        this.name = name;

        return this;
    }
}
