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
package dev.souto.v0.repository;

import dev.souto.v0.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {}
