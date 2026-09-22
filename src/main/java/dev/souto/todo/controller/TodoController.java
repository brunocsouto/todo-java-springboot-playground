package dev.souto.todo.controller;

import dev.souto.todo.dto.TodoCreateRequestDTO;
import dev.souto.todo.dto.TodoResponseDTO;
import dev.souto.todo.dto.TodoUpdateRequestDTO;
import dev.souto.todo.pagination.PageResponse;
import dev.souto.todo.pagination.SortParser;
import dev.souto.todo.service.TodoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

@RestController
@Validated
@RequestMapping("api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public PageResponse<TodoResponseDTO> findAllTodos(
        @RequestParam(defaultValue = "0") @PositiveOrZero int page,
        @RequestParam(defaultValue = "10") @Positive int size,
        @RequestParam(defaultValue = "title,asc") String sort
    ) {
        return todoService.findAll(
            page,
            size,
            SortParser.parse(
                sort,
                Set.of("title", "description")
            )
        );
    }

    @GetMapping("/{id}")
    public TodoResponseDTO findTodoById(@PathVariable String id) {
        TodoResponseDTO response = todoService.findById(id);
        return response;
    }

    @PostMapping
    public TodoResponseDTO createTodo(
        @RequestBody @Valid TodoCreateRequestDTO dto
    ) {
        TodoResponseDTO response = todoService.save(dto);
        return response;
    }

    @PutMapping("/{id}")
    public TodoResponseDTO updateTodo(
        @PathVariable String id,
        @RequestBody @Valid TodoUpdateRequestDTO dto
    ) {
        TodoResponseDTO response = todoService.update(id, dto);
        return response;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodo(@PathVariable String id) {
        todoService.delete(id);
    }
}
