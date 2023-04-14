package com.unittestapi.unittestapi.web;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unittestapi.unittestapi.dto.UserDto;
import com.unittestapi.unittestapi.entity.User;
import com.unittestapi.unittestapi.exception.UserNotFoundException;
import com.unittestapi.unittestapi.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserApiController {

	private UserService userService;
	private ModelMapper modelMapper;

	protected UserApiController(UserService userService, ModelMapper modelMapper) {
		this.userService = userService;
		this.modelMapper = modelMapper;
	}

	@PostMapping
	public ResponseEntity<?> add(@RequestBody @Valid User user) {
		User persistedUser = userService.add(user);
		URI uri = URI.create("/users/" + persistedUser.getId());
		return ResponseEntity.created(uri).body(entity2Dto(persistedUser));//pour ne pas afficher le password
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> get(@PathVariable("id") Long id) {
	   try {
		User user = userService.get(id);
		return ResponseEntity.ok(entity2Dto(user));
	} catch (UserNotFoundException e) {
		return ResponseEntity.notFound().build();
	}
	}

	@GetMapping
	public ResponseEntity<?> list() {
		List<User> users = userService.list();
		
		if(users.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(list2Dto(users));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody @Valid User user) {
		try {
			user.setId(id);
			User updateUser = userService.update(user);
			return ResponseEntity.ok(entity2Dto(updateUser));
		} catch (UserNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Long id) {
		try {
			userService.delete(id);
			return ResponseEntity.noContent().build();
		} catch (UserNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	private UserDto entity2Dto(User entity) {
		return modelMapper.map(entity, UserDto.class);
	}
	
	private List<UserDto> list2Dto(List<User> listUsers){
		return listUsers.stream().map(
				entity -> entity2Dto(entity)).collect(Collectors.toList());
	}
}
