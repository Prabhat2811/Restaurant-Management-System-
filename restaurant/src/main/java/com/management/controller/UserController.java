package com.management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.management.dto.LoginDto;
import com.management.dto.ResponseStructure;
import com.management.dto.UserDto;
import com.management.entity.User;
import com.management.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseStructure<User>> register(@RequestBody @Valid UserDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(dto));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ResponseStructure<User>> login(@RequestBody LoginDto dto) {
        return ResponseEntity.ok(userService.login(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<User>> getById(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.FOUND).body(userService.getUserById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseStructure<List<User>>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }
}
