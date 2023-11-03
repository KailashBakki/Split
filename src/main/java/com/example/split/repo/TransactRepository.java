package com.example.split.repo;

import com.example.split.entity.Transact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactRepository extends JpaRepository<Transact,Long> {
}
