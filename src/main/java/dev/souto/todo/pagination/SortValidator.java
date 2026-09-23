package dev.souto.todo.pagination;

import dev.souto.todo.exception.BusinessRulesException;
import java.util.Set;

public final class SortValidator {

    private SortValidator() {}

    public static void validate(String sort, Set<String> allowedFields) {
        if (sort == null || sort.isBlank()) {
            return;
        }

        String[] parts = sort.split(",", -1);
        if (
            parts.length != 2 ||
            !allowedFields.contains(parts[0]) ||
            (!parts[1].equalsIgnoreCase("asc") &&
                !parts[1].equalsIgnoreCase("desc"))
        ) {
            throw new BusinessRulesException(
                "Sort must use an allowed field and direction (asc or desc)"
            );
        }
    }
}
