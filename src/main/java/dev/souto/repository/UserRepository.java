/* ?:
 * <<REPOSITORY>>
 *
 * * Layer:
 *   Data access and persistence.
 *
 * * Interface:
 *   Defines the contract used by the Service layer to communicate
 *   with the persistence layer.
 *
 * * Responsibility:
 *   Provide access to User data without exposing persistence details
 *   to the business layer.
 *
 * # The interface is the communication boundary;
 *   the repository layer is the responsibility boundary.
 */

package dev.souto.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import dev.souto.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
