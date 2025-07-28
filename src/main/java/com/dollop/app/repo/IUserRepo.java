package com.dollop.app.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dollop.app.entity.User;

public interface IUserRepo extends JpaRepository<User, String> {
	boolean existsByEmail(String email);

	boolean existsByUsername(String username);

	User findByEmail(String email);

	boolean existsByEmailAndIdNot(String email, String id);

	List<User> findByIdNot(String id);
}
