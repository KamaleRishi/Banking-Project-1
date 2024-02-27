package com.bank.controller;

import java.io.FileNotFoundException;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.bank.entities.Transaction;
import com.bank.service.BankStatement;
import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/bankStatement")
@AllArgsConstructor
@Tag(name="BANK STATEMENT MANAGEMENT API'S")
public class TransactionController {

	private BankStatement bankStatement;
	
	@GetMapping
	@Operation(
			summary = "GENERATING BANK STATEMENT",
			description = "GENERATION BANK STATMENTS FOR CUSTOMER WITH BANK ACCOUNT NUMBER"
			)
	@ApiResponse(
			responseCode = "201",
			description = "HTTP STATUS 201 CREATED"
			)
	public List<Transaction> generateBankStatement(@RequestParam String accountNumber, @RequestParam String startDate, @RequestParam String endDate) throws FileNotFoundException, DocumentException{
		
		return bankStatement.generateStatement(accountNumber, startDate, endDate);
	}
}
