/* ?:
 * <<REPOSITORY>>
 *
 * INFO Layer:
 *   Data access and persistence.
 *
 * INFO Interface:
 *   Defines the contract used by the Service layer to communicate
 *   with the persistence layer.
 *
 * INFO Responsibility:
 *   Provide access to User data without exposing persistence details
 *   to the business layer.
 *
 * INFO The interface is the communication boundary;
 *   the repository layer is the responsibility boundary.
 */
package dev.souto.v0.repository;

import org.apache.catalina.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    //TODO Add custom repository queries when required
}
