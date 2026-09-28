/* ?:
 * <<MODEL>>
 *
 * * Layer:
 *   Domain data and entity representation.
 *
 * * Interface:
 *   Defines the data structure and behavior exposed to other
 *   application components.
 *
 * * Responsibility:
 *   Represent domain entities, their attributes, relationships,
 *   and domain-specific behavior.
 *
 * # The model defines the domain representation;
 *   its responsibility is to represent and protect domain state.
 */

package dev.souto.v0.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Getter
public class User {

    @Id
    private String id;

    private String name;
    private String email;
    private String password;
    private boolean isActive;

    private User(String name, String email, String password, boolean isActive) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isActive = isActive;

        validateName(name);
        validateEmail(email);
        validatePassword(password);
    }

    public static User create(String name, String email, String password) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException(
                "Name, email, and password are required"
            );
        }
        return new User(name, email, password, false);
    }

    public User update(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;

        validateName(this.name);
        validateEmail(this.email);
        validatePassword(this.password);

        return this;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    private void validateName(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (name.length() > 255) {
            throw new IllegalArgumentException(
                "Name must be less than 255 characters"
            );
        }
    }

    private void validateEmail(String email) {
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!email.matches("[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}")) {
            throw new IllegalArgumentException(
                "Email must be a valid email address"
            );
        }
    }

    private void validatePassword(String password) {
        if (password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (password.length() < 8 || password.length() > 32) {
            throw new IllegalArgumentException(
                "Password must be between 8 and 32 characters"
            );
        }
    }
}
