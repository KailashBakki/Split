package com.example.split.repo;

import com.example.split.entity.Transact;
import com.example.split.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
