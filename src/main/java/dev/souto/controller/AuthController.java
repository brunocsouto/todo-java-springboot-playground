package dev.souto.controller;

import dev.souto.config.TokenConfig;
import dev.souto.dto.LoginRequestDTO;
import dev.souto.dto.LoginResponseDTO;
import dev.souto.dto.RegisterUserRequestDTO;
import dev.souto.dto.RegisterUserResponseDTO;
import dev.souto.entity.User;
import dev.souto.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenConfig tokenConfig;

    public AuthController(
        AuthenticationManager authenticationManager,
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        TokenConfig tokenConfig
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenConfig = tokenConfig;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
        @Valid @RequestBody LoginRequestDTO request
    ) {
        UsernamePasswordAuthenticationToken userAndPass =
            new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
            );

        Authentication authentication = authenticationManager.authenticate(
            userAndPass
        );

        User user = (User) authentication.getPrincipal();

        String token = tokenConfig.generateToken(user);

        return ResponseEntity.status(HttpStatus.OK).body(
            LoginResponseDTO.of(token)
        );
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDTO> register(
        @Valid @RequestBody RegisterUserRequestDTO request
    ) {
        User newUser = User.create(
            request.name(),
            request.email(),
            passwordEncoder.encode(request.password())
        );

        if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        userRepository.save(newUser);

        RegisterUserResponseDTO response = RegisterUserResponseDTO.of(
            newUser.getName(),
            newUser.getEmail()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
