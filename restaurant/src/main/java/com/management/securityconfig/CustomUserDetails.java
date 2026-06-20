package com.management.securityconfig;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.management.entity.User;

import io.micrometer.common.lang.Nullable;

public class CustomUserDetails implements UserDetails {
	
	private User users;
	
	public CustomUserDetails(User users) {
		this.users=users;
	}
	
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return Arrays.asList(new SimpleGrantedAuthority(users.getRole().name()));
	}

	@Override
	public @Nullable String getPassword() {
		
		return users.getPassword();
	}

	@Override
	public String getUsername() {
		
		return users.getEmail();
	}

}
