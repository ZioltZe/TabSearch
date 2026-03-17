package eliott.backend.controller;

import eliott.backend.model.user.User;
import eliott.backend.model.user.dto.UserCredentials;
import eliott.backend.repository.UserRepository;
import eliott.backend.security.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/auth")
public class AuthController
{
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/login")
    public Map<String, Object> loginHandler(@RequestBody UserCredentials body)
    {
        try
        {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword());
            authenticationManager.authenticate(authToken);
            User user = userRepository.findByEmail(body.getEmail())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found"));
            String token = jwtUtil.generateToken(user, 1000*60*60*2);
            return Collections.singletonMap("jwt-token", token);
        } catch (NoSuchElementException e) {throw new RuntimeException("Invalid email or password", e);}
    }
}
