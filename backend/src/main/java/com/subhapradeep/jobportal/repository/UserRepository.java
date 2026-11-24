package com.subhapradeep.jobportal.repository;

import com.subhapradeep.jobportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data builds the SQL from the method name
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
