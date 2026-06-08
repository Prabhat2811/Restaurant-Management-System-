package com.management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
	private String name;
    @NotBlank private String email;
    @NotBlank private String password;
    private String phone;
    private Role role;
}
