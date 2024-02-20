package com.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.bank.dto.TransactionDto;
import com.bank.entities.Transaction;
import com.bank.repository.TransactionsRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TransactionServiceImpl implements TransactionService{

	@Autowired
	private TransactionsRepository transactionsRepository;
	
	@Override
	public void saveTransaction(TransactionDto transactionDto) {
		Transaction transaction = Transaction.builder()
				.transactionType(transactionDto.getTransactionType())
				.accountNumber(transactionDto.getAccountNumber())
				.amount(transactionDto.getAmount())
				.status("SUCCESS")
				.build();
		transactionsRepository.save(transaction);
		log.info("Transaction saved sucessfully");
	}

}
