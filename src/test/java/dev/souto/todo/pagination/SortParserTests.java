package dev.souto.todo.pagination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.souto.todo.exception.BusinessRulesException;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

class SortParserTests {

    @Test
    void shouldParseAscendingSort() {
        Sort sort = SortParser.parse("title,asc", Set.of("title"));

        assertEquals(Sort.Direction.ASC, sort.getOrderFor("title").getDirection());
    }

    @Test
    void shouldParseDescendingSortCaseInsensitively() {
        Sort sort = SortParser.parse("name,DESC", Set.of("name"));

        assertEquals(
            Sort.Direction.DESC,
            sort.getOrderFor("name").getDirection()
        );
    }

    @Test
    void shouldRejectUnsupportedProperty() {
        assertThrows(
            BusinessRulesException.class,
            () -> SortParser.parse("id,asc", Set.of("name"))
        );
    }

    @Test
    void shouldRejectInvalidDirection() {
        assertThrows(
            BusinessRulesException.class,
            () -> SortParser.parse("name,sideways", Set.of("name"))
        );
    }
}
