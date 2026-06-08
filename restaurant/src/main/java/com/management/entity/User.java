package com.management.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.management.dto.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name= "users")
public class User {
	 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;
	    private String name;
	    @Column(unique = true) private String email;
	    private String password;
	    @Column(unique = true) private String phone;
	    @Enumerated(EnumType.STRING) private Role role;
	    @CreationTimestamp private LocalDateTime createdAt;
}
