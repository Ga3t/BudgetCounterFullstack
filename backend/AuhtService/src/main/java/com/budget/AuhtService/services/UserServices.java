package com.budget.AuhtService.services;

import com.budget.AuhtService.models.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


@Service
public interface UserServices extends UserDetailsService{
	UserEntity saveUser(UserEntity user);
	UserEntity findById(Long id);
	UserEntity findByUsername(String username);
}
