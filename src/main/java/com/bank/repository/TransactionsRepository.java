package com.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.entities.Transaction;

public interface TransactionsRepository extends JpaRepository<Transaction, String>{

}
