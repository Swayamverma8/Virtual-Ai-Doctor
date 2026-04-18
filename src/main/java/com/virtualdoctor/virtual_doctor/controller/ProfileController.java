package com.virtualdoctor.virtual_doctor.controller;

import com.virtualdoctor.virtual_doctor.model.User;
import com.virtualdoctor.virtual_doctor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<User> getProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<String> updateProfile(
            Authentication authentication,
            @RequestBody Map<String, String> request) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        if (request.containsKey("name") && !request.get("name").isEmpty())
            user.setName(request.get("name"));

        if (request.containsKey("age") && !request.get("age").isEmpty())
            user.setAge(Integer.parseInt(request.get("age")));

        if (request.containsKey("bloodGroup"))
            user.setBloodGroup(request.get("bloodGroup"));

        if (request.containsKey("allergies"))
            user.setAllergies(request.get("allergies"));

        if (request.containsKey("medicalHistory"))
            user.setMedicalHistory(request.get("medicalHistory"));

        userRepository.save(user);
        return ResponseEntity.ok("Profile updated successfully!");
    }
}