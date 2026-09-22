package dev.souto.todo.pagination;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponse<T>(
    @JsonProperty("items") List<T> content,
    int page,
    int size,
    long totalElements,
    boolean hasNext
) {
    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<T>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.hasNext()
        );
    }
}
