package com.bank.dto;

import java.math.BigDecimal;

import com.bank.utlis.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {

	private String transactionId;
	private TransactionType transactionType;
	private BigDecimal amount;
	private String accountNumber;
	private String status;
}
