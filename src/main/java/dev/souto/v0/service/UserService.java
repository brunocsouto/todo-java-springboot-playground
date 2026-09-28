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

package dev.souto.v0.service;

import dev.souto.v0.model.User;
import dev.souto.v0.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        List<User> users = userRepository.findAll();

        return users;
    }

    public Optional<User> findById(String id) {
        User user = userRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return Optional.of(user);
    }

    public User save(String name, String email, String password) {
        User user = User.create(name, email, password);

        User savedUser = userRepository.save(user);

        return savedUser;
    }

    public User update(String id, String name, String email, String password) {
        User updatedUser = userRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"))
            .update(name, email, password);

        return userRepository.save(updatedUser);
    }

    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    public User activateById(String id) {
        User user = userRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.activate();

        User savedUser = userRepository.save(user);

        return savedUser;
    }

    public User deactivateById(String id) {
        User user = userRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.deactivate();

        User savedUser = userRepository.save(user);

        return savedUser;
    }
}
