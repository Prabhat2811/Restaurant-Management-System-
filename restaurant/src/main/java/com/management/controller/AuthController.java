package com.management.controller;

import com.management.dto.LoginRequest;
import com.management.dto.RegisterRequest;
import com.management.dto.Role;
import com.management.entity.Customer;
import com.management.entity.User;
import com.management.repository.CustomerRepository;
import com.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "No account found with this email.");
            return ResponseEntity.status(401).body(response);
        }

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(), request.getPassword()
                )
            );
        } catch (BadCredentialsException e) {
            response.put("success", false);
            response.put("message", "Incorrect password.");
            return ResponseEntity.status(401).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Authentication failed. Please try again.");
            return ResponseEntity.status(500).body(response);
        }

        User user = userOpt.get();

        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", user.getId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("role", user.getRole().name());
        userData.put("phone", user.getPhone());

        // CUSTOMER: return customerId (from customers table) as "id"
        // ADMIN/RESTAURANT_ADMIN: return userId as "id"
        if (user.getRole() == Role.CUSTOMER) {
            customerRepository.findByUser_Id(user.getId()).ifPresent(customer ->
                userData.put("id", customer.getId())
            );
            // Fallback if customer row missing (old accounts)
            if (!userData.containsKey("id")) {
                Customer customer = new Customer();
                customer.setUser(user);
                Customer saved = customerRepository.save(customer);
                userData.put("id", saved.getId());
            }
        } else {
            userData.put("id", user.getId());
        }

        response.put("success", true);
        response.put("message", "Login successful.");
        response.put("data", userData);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            response.put("success", false);
            response.put("message", "An account with this email already exists.");
            return ResponseEntity.status(409).body(response);
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole() != null ? request.getRole().toUpperCase() : "CUSTOMER");
            if (role == Role.ADMIN) {
                response.put("success", false);
                response.put("message", "Cannot self-register as ADMIN.");
                return ResponseEntity.status(403).body(response);
            }
        } catch (IllegalArgumentException e) {
            role = Role.CUSTOMER;
        }

        // Save User
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(role);
        User savedUser = userRepository.save(user);

        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", savedUser.getId());
        userData.put("name", savedUser.getName());
        userData.put("email", savedUser.getEmail());
        userData.put("role", savedUser.getRole().name());

        // If CUSTOMER — create Customer row linked to User
        if (role == Role.CUSTOMER) {
            Customer customer = new Customer();
            customer.setUser(savedUser);
            Customer savedCustomer = customerRepository.save(customer);
            userData.put("id", savedCustomer.getId());  // this is what order.html uses
        } else {
            userData.put("id", savedUser.getId());
        }

        response.put("success", true);
        response.put("message", "Account created successfully.");
        response.put("data", userData);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        boolean exists = userRepository.findByEmail(email).isPresent();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/setup-admin")
    public ResponseEntity<Map<String, Object>> setupAdmin() {
        Map<String, Object> response = new HashMap<>();

        // Only create if no admin exists
        if (userRepository.findByEmail("admin@savor.com").isPresent()) {
            response.put("success", false);
            response.put("message", "Admin already exists.");
            return ResponseEntity.status(409).body(response);
        }

        User admin = new User();
        admin.setName("Prabhat Ranjan");
        admin.setEmail("ranjanprabhat.c@gmail.com");
        admin.setPassword(passwordEncoder.encode("Prabhat@123"));
        admin.setPhone("9693711738");
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        response.put("success", true);
        response.put("message", "Admin created. Email: ranjanprabhat.c.com | Password: Prabhat@123");
        return ResponseEntity.ok(response);
    }
}