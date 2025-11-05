package com.learn.app.demo.controller;

import com.learn.app.demo.entity.User;
import com.learn.app.demo.enums.UserStatus;
import com.learn.app.demo.repository.UserRepository;
import com.learn.app.demo.request.LoginRequest;
import com.learn.app.demo.request.SignUpRequest;
import com.learn.app.demo.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository users;
    private final JwtService jwt;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository users, JwtService jwt, PasswordEncoder encoder) {
        this.users = users;
        this.jwt = jwt;
        this.encoder = encoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest req) {
        if (users.existsByEmail(req.getEmail().toLowerCase())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("success", false, "message", "Email already exists"));
        }
        if (users.existsByPhone(req.getPhone())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("success", false, "message", "Phone already exists"));
        }

        User u = new User();
        u.setFirstName(req.getFirstName().trim());
        u.setLastName(req.getLastName().trim());
        u.setEmail(req.getEmail().toLowerCase());
        u.setPhone(req.getPhone());
        u.setGender(req.getGender());
        u.setStatus(UserStatus.PENDING); // change to APPROVED if you want immediate login
        u.setPasswordHash(encoder.encode(req.getPassword()));

        users.save(u);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "success", 1,
                        "data", Map.of(
                                "message", "User created successfully",
                                "userId", u.getId()
                        )
                )
        );

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        var u = users.findByEmail(req.getEmail().toLowerCase()).orElse(null);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid credentials"));
        }
        if (!encoder.matches(req.getPassword(), u.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid credentials"));
        }
        if (u.getStatus() != UserStatus.APPROVED) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "message", "Account not approved"));
        }

        String token = jwt.generateToken(u.getId());
        return ResponseEntity.ok(
                Map.of(
                        "token", token,
                        "type", "Bearer",
                        "expiresIn", 3600,
                        "user", Map.of(
                                "id", u.getId(),
                                "firstName", u.getFirstName(),
                                "lastName", u.getLastName(),
                                "email", u.getEmail(),
                                "phone", u.getPhone(),
                                "gender", u.getGender().name()
                        )
                )

        );

    }
}
