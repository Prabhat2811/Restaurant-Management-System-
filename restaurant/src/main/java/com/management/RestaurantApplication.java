package com.management;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.management.entity.User;
import com.management.repository.UserRepository;

@SpringBootApplication
public class RestaurantApplication {
	public static void main(String[] args) {
		SpringApplication.run(RestaurantApplication.class, args);
	}
	 @Bean
	    public CommandLineRunner encodePasswords(UserRepository userRepository, PasswordEncoder passwordEncoder) {
	        return args -> {
	            List<User> users = userRepository.findAll();
	            for (User user : users) {
	                if (!user.getPassword().startsWith("$2a$")) {
	                    user.setPassword(passwordEncoder.encode(user.getPassword()));
	                    userRepository.save(user);
	                    System.out.println("Encoded password for: " + user.getEmail());
	                }
	            }
	        };
	 }
}
