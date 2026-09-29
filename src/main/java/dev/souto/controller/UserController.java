/* ?:
 * <<CONTROLLER>>
 *
 * * Layer:
 *   HTTP request handling and presentation.
 *
 * * Interface:
 *   Defines the HTTP communication boundary between external clients
 *   and the application.
 *
 * * Responsibility:
 *   Receive requests, delegate operations to the Service layer,
 *   and return appropriate HTTP responses.
 *
 * # The HTTP API is the communication boundary;
 *   the controller layer is the presentation responsibility boundary.
 */

package dev.souto.controller;

import dev.souto.dto.UserRequestDTO;
import dev.souto.dto.UserResponseDTO;
import dev.souto.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v0/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Page<UserResponseDTO> getUsers(
        @PageableDefault(
            direction = Sort.Direction.ASC,
            sort = "name"
        ) Pageable pageable
    ) {
        return userService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    public UserResponseDTO createUser(
        @RequestBody @Valid UserRequestDTO request
    ) {
        return userService.save(
            request.name(),
            request.email(),
            request.password()
        );
    }

    @PutMapping("/{id}")
    public UserResponseDTO updateUser(
        @PathVariable Long id,
        @RequestBody @Valid UserRequestDTO request
    ) {
        return userService.update(
            id,
            request.name(),
            request.email(),
            request.password()
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
    }

    @PatchMapping("/activate/{id}")
    public UserResponseDTO activateUser(@PathVariable Long id) {
        return userService.activateById(id);
    }

    @PatchMapping("/deactivate/{id}")
    public UserResponseDTO deactivateUser(@PathVariable Long id) {
        return userService.deactivateById(id);
    }
}
