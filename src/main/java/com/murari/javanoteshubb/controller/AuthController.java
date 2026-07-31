package com.murari.javanoteshubb.controller;


import com.murari.javanoteshubb.entity.User;
import com.murari.javanoteshubb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.UUID;

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
        if (user.getEmail() != null && user.getEmail().equalsIgnoreCase("pandeymurari571@gmail.com")) {
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

        return ResponseEntity.status(401).body("Wrong Please try again!");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String,String> request) {

        String email = request.get("email");

        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Email registered nahi hai!");
        }

        User user = userOptional.get();

        String token = UUID.randomUUID().toString();

        user.setResetToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(15));

        userRepository.save(user);


        Map<String,String> response = new HashMap<>();

        response.put(
                "message",
                "Reset token generated successfully!"
        );

        response.put(
                "token",
                token
        );


        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String,String> request) {

        String token = request.get("token");
        String newPassword = request.get("password");

        Optional<User> userOptional = userRepository.findByResetToken(token);

        if(userOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Invalid reset token!");
        }

        User user = userOptional.get();

        if(user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity
                    .badRequest()
                    .body("Reset token expired!");
        }


        user.setPassword(newPassword);

        user.setResetToken(null);
        user.setTokenExpiry(null);

        userRepository.save(user);


        return ResponseEntity.ok(
                Map.of("message","Password updated successfully!")
        );
    }
}