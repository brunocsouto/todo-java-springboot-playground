package dev.souto.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.souto.entity.User;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class TokenConfig {

    protected final String secret = "secret";

    Algorithm algorithm = Algorithm.HMAC256(secret);

    public String generateToken(User user) {
        return JWT.create()
            .withClaim("id", user.getId())
            .withClaim("name", user.getName())
            .withClaim("email", user.getEmail())
            .sign(algorithm);
    }

    public Optional<JWTUserData> validateToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(algorithm)
                .build()
                .verify(token);

            Long id = decodedJWT.getClaim("id").asLong();
            String name = decodedJWT.getClaim("name").asString();
            String email = decodedJWT.getClaim("email").asString();

            if (id == null || name == null || email == null) {
                return Optional.empty();
            }

            return Optional.of(new JWTUserData(id, name, email));
        } catch (
            JWTVerificationException
            | IllegalArgumentException exception
        ) {
            return Optional.empty();
        }
    }
}
