package com.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.management.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
