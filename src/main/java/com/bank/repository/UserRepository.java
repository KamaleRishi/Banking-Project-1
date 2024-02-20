package com.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.entities.User;

public interface UserRepository extends JpaRepository<User, Long>{

    boolean existsByEmail(String email);
    
    List<User> findByEmail(String email);
    
    boolean existsByAccountNumber(String accountNumber);
    
    User findByAccountNumber(String accountNumber);
}
