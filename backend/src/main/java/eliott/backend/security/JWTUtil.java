package eliott.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import eliott.backend.model.role.Role;
import eliott.backend.model.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JWTUtil
{
    @Value("${jwt_secret}")
    private String secret;

    /**
     * This method will generate a JWT token using HMAC
     * @param user the user
     * @param expiresIn the time in milliseconds when the token expires
     * @return a JWT token
     */
    public String generateToken(User user, int expiresIn) throws IllegalArgumentException, JWTCreationException
    {
        List<String> roles = user.getRoles().stream().map(Role::getName).toList();
        return JWT.create()
                .withSubject("User Details")
                .withClaim("email", user.getEmail())
                .withClaim("roles", roles)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiresIn))
                .withIssuer("ELIOTT")
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * This method will verify a token
     * @param token the token
     * @return the email of the user
     */
    public String validateToken(String token) throws JWTVerificationException
    {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User Details")
                .withIssuer("ELIOTT")
                .build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("email").asString();
    }
}
