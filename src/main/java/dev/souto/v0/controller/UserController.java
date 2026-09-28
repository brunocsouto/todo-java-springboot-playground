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

package dev.souto.v0.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.souto.v0.model.User;
import dev.souto.v0.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("api/v0/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> getUsers() {
        List<UserResponse> users = userService
            .findAll()
            .stream()
            .map(UserResponse::of)
            .toList();

        return users;
    }

    @GetMapping("/{id}")
    public Optional<UserResponse> getUserById(@PathVariable @NotBlank String id) {
        return userService.findById(id).map(UserResponse::of);
    }

    @PostMapping
    public UserResponse createUser(@RequestBody @Valid InnerUserController request) {
        User user = userService.save(
            request.name,
            request.email,
            request.password
        );

        return UserResponse.of(user);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(
        @PathVariable @NotBlank String id,
        @RequestBody InnerUserController request
    ) {
        User user = userService.update(
            id,
            request.name,
            request.email,
            request.password
        );

        return UserResponse.of(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable @NotBlank String id) {
        userService.deleteById(id);
    }

    @PatchMapping("/activate/{id}")
    public UserResponse activateUser(@PathVariable @NotBlank String id) {
        User user = userService.activateById(id);

        return UserResponse.of(user);
    }

    @PatchMapping("/deactivate/{id}")
    public UserResponse deactivateUser(@PathVariable @NotBlank String id) {
        User user = userService.deactivateById(id);

        return UserResponse.of(user);
    }

    private record InnerUserController(
        String name,
        String email,
        String password
    ) {}

    private record UserResponse(
        String id,
        String name,
        String email,
        boolean isActive
    ) {
        public static UserResponse of(User user) {
            return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.isActive()
            );
        }
    }
}
