package com.virtualdoctor.virtual_doctor.service;

import com.virtualdoctor.virtual_doctor.model.User;
import com.virtualdoctor.virtual_doctor.repository.UserRepository;
import com.virtualdoctor.virtual_doctor.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(String name, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            return "Email already exists!";
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return "User registered successfully!";
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return "User not found!";
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "Invalid password!";
        }
        return jwtUtil.generateToken(email);
    }
}