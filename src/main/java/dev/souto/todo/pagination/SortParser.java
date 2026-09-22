package dev.souto.todo.pagination;

import dev.souto.todo.exception.BusinessRulesException;
import java.util.Set;
import org.springframework.data.domain.Sort;

public final class SortParser {

    private SortParser() {}

    public static Sort parse(String value, Set<String> allowedProperties) {
        String[] parts = value.split(",", -1);

        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new BusinessRulesException(
                "Sort must use the format field,direction"
            );
        }

        String property = parts[0].trim();
        String direction = parts[1].trim().toLowerCase();

        if (!allowedProperties.contains(property)) {
            throw new BusinessRulesException(
                "Sorting by '" + property + "' is not supported"
            );
        }

        if (!direction.equals("asc") && !direction.equals("desc")) {
            throw new BusinessRulesException(
                "Sort direction must be either asc or desc"
            );
        }

        Sort.Direction sortDirection = direction.equals("asc")
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;

        return Sort.by(sortDirection, property);
    }
}
