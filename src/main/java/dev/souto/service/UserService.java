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

import dev.souto.dto.UserResponseDTO;
import dev.souto.entity.User;
import dev.souto.exception.BusinessLogicException;
import dev.souto.exception.ResourceNotFoundException;
import dev.souto.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<UserResponseDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponseDTO::of);
    }

    public UserResponseDTO findById(Long id) {
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
        Long id,
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

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public UserResponseDTO activateById(Long id) {
        User user = findByIdOrThrow(id);

        user.activate();

        return UserResponseDTO.of(userRepository.save(user));
    }

    public UserResponseDTO deactivateById(Long id) {
        User user = findByIdOrThrow(id);

        user.deactivate();

        return UserResponseDTO.of(userRepository.save(user));
    }

    // private methods
    private User findByIdOrThrow(Long id) {
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
