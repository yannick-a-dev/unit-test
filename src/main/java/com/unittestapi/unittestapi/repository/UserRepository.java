package com.unittestapi.unittestapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.unittestapi.unittestapi.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
