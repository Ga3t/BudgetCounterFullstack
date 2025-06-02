package com.budget.AuhtService.services.impl;

import com.budget.AuhtService.models.UserEntity;
import com.budget.AuhtService.repository.UserRepository;
import com.budget.AuhtService.services.UserServices;
import com.budget.core.exceptions.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
@Primary
@AllArgsConstructor
public class UserServicesImplement implements UserServices {
	
	private final UserRepository repository;

	@Override
	public UserEntity saveUser(UserEntity user) {
		return repository.save(user);
	}

	@Override
	public UserEntity findById(Long id) {

		return null;
	}

	@Override
	public UserEntity findByUsername(String username) {

		return repository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User not found"));
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = repository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("Username not found"));
		return new User(user.getUsername(),
				user.getPassword(), 
				Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
				);
	}

}
