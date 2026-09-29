/* ?:
 * <<SERVICE>>
 *
 * * Layer:
 *   Business logic and application orchestration.
 *
 * * Interface:
 *   Defines the operations used by the Controller layer to communicate
 *   with the business layer.
 *
 * * Responsibility:
 *   Execute use cases and coordinate business operations without exposing
 *   business logic to the presentation layer.
 *
 * # The service interface is the communication boundary;
 *   the service layer is the business responsibility boundary.
 */

package dev.souto.service;

import dev.souto.dtos.UserResponseDTO;
import dev.souto.error.BusinessLogicException;
import dev.souto.error.ResourceNotFoundException;
import dev.souto.model.User;
import dev.souto.repository.UserRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponseDTO> findAll() {
        List<User> users = userRepository.findAll();

        return users.stream().map(UserResponseDTO::of).toList();
    }

    public UserResponseDTO findById(String id) {
        return userRepository
            .findById(id)
            .map(UserResponseDTO::of)
            .orElseThrow(() ->
                new ResourceNotFoundException("User not found: " + id)
            );
    }

    public UserResponseDTO save(String name, String email, String password) {
        String passwordHash = passwordEncoder.encode(password);

        User user = User.create(name, email, passwordHash);

        throwIfEmailExists(email);

        return UserResponseDTO.of(userRepository.save(user));
    }

    public UserResponseDTO update(
        String id,
        String name,
        String email,
        String password
    ) {
        String passwordHash = passwordEncoder.encode(password);

        User updatedUser = findByIdOrThrow(id).update(
            name,
            email,
            passwordHash
        );

        throwIfEmailExists(email);

        return UserResponseDTO.of(userRepository.save(updatedUser));
    }

    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    public UserResponseDTO activateById(String id) {
        User user = findByIdOrThrow(id);

        user.activate();

        return UserResponseDTO.of(userRepository.save(user));
    }

    public UserResponseDTO deactivateById(String id) {
        User user = findByIdOrThrow(id);

        user.deactivate();

        return UserResponseDTO.of(userRepository.save(user));
    }

    // private methods
    private User findByIdOrThrow(String id) {
        return userRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void throwIfEmailExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessLogicException("Email already exists");
        }
    }
}
