package dev.souto.config;

import dev.souto.entity.User;
import dev.souto.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedUsers(
        UserRepository repository,
        PasswordEncoder passwordEncoder
    ) {
        return args -> {
            createUser(
                repository,
                passwordEncoder,
                "John Doe",
                "john@example.com",
                "password123"
            );

            createUser(
                repository,
                passwordEncoder,
                "Jane Doe",
                "jane@example.com",
                "password123"
            );
        };
    }

    private void createUser(
        UserRepository repository,
        PasswordEncoder passwordEncoder,
        String name,
        String email,
        String rawPassword
    ) {
        if (repository.existsByEmail(email)) {
            return;
        }

        User user = User.create(
            name,
            email,
            passwordEncoder.encode(rawPassword)
        );
        user.activate();
        repository.save(user);
    }
}
