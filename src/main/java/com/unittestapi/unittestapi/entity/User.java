package com.unittestapi.unittestapi.entity;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length = 50, nullable = false, unique = true)
	@NotBlank(message = "E-mail address must not be empty")
	@Email(message = "User must have valid email address")
	private String email;
	
	@Column(length = 20, nullable = false)
	@Length(min = 3, max = 20)
	private String firstName;
	
	@Column(length = 20, nullable = false)
	@Length(min = 3, max = 20)
	private String lastName;
	
	@Column(length = 20, nullable = false)
	@Length(min = 6, max = 10)
	private String password;
}
