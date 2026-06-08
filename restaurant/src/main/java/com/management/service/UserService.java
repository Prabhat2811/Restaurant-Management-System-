package com.management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.management.dto.ResponseStructure;
import com.management.dto.UserDto;
import com.management.entity.User;
import com.management.exception.DuplicateEntryException;
import com.management.exception.IdNotFoundException;
import com.management.exception.ResourceNotFoundException;
import com.management.repository.UserRepository;

public class UserService {
	@Autowired
	private UserRepository
	userRepository;

    public ResponseStructure<User> registerUser(UserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new DuplicateEntryException("Email already exists: " + dto.getEmail());
        if (userRepository.existsByPhone(dto.getPhone()))
            throw new DuplicateEntryException("Phone already exists: " + dto.getPhone());

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());

        return ResponseStructure.<User>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("User Registered Successfully")
                .data(userRepository.save(user)).build();
    }

    public ResponseStructure<User> getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("User Not Found with Id: " + id));
        return ResponseStructure.<User>builder()
                .statusCode(HttpStatus.FOUND.value())
                .message("User Found")
                .data(user).build();
    }

    public ResponseStructure<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) throw new ResourceNotFoundException("No Users Found");
        return ResponseStructure.<List<User>>builder()
                .statusCode(HttpStatus.FOUND.value())
                .message(users.size() + " User(s) Found")
                .data(users).build();
    }
}
