package dev.souto.todo.controller;

import dev.souto.todo.dto.TodoCreateRequestDTO;
import dev.souto.todo.dto.TodoResponseDTO;
import dev.souto.todo.dto.TodoUpdateRequestDTO;
import dev.souto.todo.pagination.SortValidator;
import dev.souto.todo.service.TodoService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public Page<TodoResponseDTO> findAllTodos(
        @PageableDefault Pageable pageable,
        HttpServletRequest request
    ) {
        SortValidator.validate(
            request.getParameter("sort"),
            Set.of("title", "description")
        );
        return todoService.findAll(pageable);
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
