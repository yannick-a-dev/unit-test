package com.unittestapi.unittestapi.service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unittestapi.unittestapi.entity.User;
import com.unittestapi.unittestapi.exception.UserNotFoundException;
import com.unittestapi.unittestapi.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	public User add(User user) {
		return userRepository.save(user);
	}
	
	public User update(User user) throws UserNotFoundException {
		if(!userRepository.existsById(user.getId())) {
			throw new UserNotFoundException();
		}
		return userRepository.save(user);
	}
	
	public User get(Long id) throws UserNotFoundException {
	   Optional<User> result = userRepository.findById(id);
	   
	   if(result.isPresent()) {
		   return result.get();
	   }
	   throw new UserNotFoundException();
	}
	
	public List<User> list(){
		return userRepository.findAll();
	}
	
	public void delete(Long id) throws UserNotFoundException {
		if(userRepository.existsById(id)) {
			userRepository.deleteById(id);
		}
		throw new UserNotFoundException();
	}
}
