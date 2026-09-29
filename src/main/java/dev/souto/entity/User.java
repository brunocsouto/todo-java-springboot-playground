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

package dev.souto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Getter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;

    @Column(name = "is_active")
    private boolean isActive;

    private User(String name, String email, String password, boolean isActive) {
        validateName(name);
        validateEmail(email);

        this.name = name;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
    }

    protected User() {}

    public static User create(String name, String email, String password) {
        return new User(name, email, password, false);
    }

    public User update(String name, String email, String password) {
        validateName(this.name);
        validateEmail(this.email);

        this.name = name;
        this.email = email;
        this.password = password;

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
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
