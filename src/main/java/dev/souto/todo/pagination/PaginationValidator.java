package dev.souto.todo.pagination;

import dev.souto.todo.exception.BusinessRulesException;

public final class PaginationValidator {

    private static final int MAX_PAGE_SIZE = 100;

    private PaginationValidator() {}

    public static void validate(String page, String size) {
        try {
            if (page != null && Integer.parseInt(page) < 0) {
                throw invalid();
            }
            if (size != null) {
                int parsedSize = Integer.parseInt(size);
                if (parsedSize < 1 || parsedSize > MAX_PAGE_SIZE) {
                    throw invalid();
                }
            }
        } catch (NumberFormatException exception) {
            throw invalid();
        }
    }

    private static BusinessRulesException invalid() {
        return new BusinessRulesException(
            "Page must be non-negative and size must be between 1 and 100"
        );
    }
}
