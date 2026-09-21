package dev.souto.todo.controller;

import dev.souto.todo.dto.TodoRequestDTO;
import dev.souto.todo.dto.TodoResponseDTO;
import dev.souto.todo.service.TodoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<TodoResponseDTO> findAllTodos() {
        return todoService.findAll();
    }

    @GetMapping("/{id}")
    public TodoResponseDTO findTodoById(@PathVariable UUID id) {
        TodoResponseDTO response = todoService.findById(id);
        return response;
    }

    @GetMapping("/search/{title}")
    public TodoResponseDTO findTodoByTitle(@PathVariable @NotBlank String title) {
        TodoResponseDTO response = todoService.findByTitle(title);
        return response;
    }

    @PostMapping
    public TodoResponseDTO createTodo(@Valid @RequestBody TodoRequestDTO dto) {
        TodoResponseDTO response = todoService.save(dto);
        return response;
    }

    @PutMapping("/{id}")
    public TodoResponseDTO updateTodo(
        @PathVariable UUID id,
        @Valid @RequestBody TodoRequestDTO dto
    ) {
        TodoResponseDTO response = todoService.update(id, dto);
        return response;
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable UUID id) {
        todoService.delete(id);
    }
}
