package com.murari.javanoteshubb.controller;


import com.murari.javanoteshubb.entity.User;
import com.murari.javanoteshubb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // 1. SIGNUP API
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email pehle se registered hai!");
        }

        // Default Role "STUDENT" rahega. Pehla user banate waqt aap manually DB me "ADMIN" kar sakte ho.
        // Admin email check
        if (user.getEmail() != null && user.getEmail().equalsIgnoreCase("pandeymurari768@gmail.com")) {
            user.setRole("ADMIN");
        } else if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("STUDENT");
        }

        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User successfully registered!");
        return ResponseEntity.ok(response);
    }

    // 2. LOGIN API
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginData) {
        Optional<User> userOptional = userRepository.findByEmail(loginData.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Checking simple password
            if (user.getPassword().equals(loginData.getPassword())) {
                Map<String, Object> response = new HashMap<>();
                response.put("id", user.getId());
                response.put("name", user.getName());
                response.put("email", user.getEmail());
                response.put("role", user.getRole()); // "ADMIN" ya "STUDENT"

                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(401).body("Galat Email ya Password!");
    }
}