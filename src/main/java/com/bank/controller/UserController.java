package com.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.Response.BankResponse;
import com.bank.dto.CreditRequest;
import com.bank.dto.DebitRequest;
import com.bank.dto.EnquiryRequest;
import com.bank.dto.LoginDto;
import com.bank.dto.TransferAmountRequest;
import com.bank.dto.UserRequest;
import com.bank.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user")
@Tag(name="USER ACCOUNT MANAGEMENT API'S")
public class UserController {

	@Autowired
	UserService userService;
	
	@PostMapping
	@Operation(
			summary = "CREATE NEW USER BANK ACCOUNT",
			description = "CREATING A NEW USER AND ASSIGING A BANK ACCOUNT NUMBER"
			)
	@ApiResponse(
			responseCode = "201",
			description = "HTTP STATUS 201 CREATED"
			)
	public BankResponse createdAccountNumber(@RequestBody UserRequest userRequest) {
		return userService.createAccount(userRequest);
	}
	
	@PostMapping("/login")
	public BankResponse login(@RequestBody LoginDto loginDto) {
		return userService.login(loginDto);
	}
	
	@GetMapping("/{balanceEnquiry}")
	@Operation(
			summary = "BALANCE ENQUIRY",
			description = "BALANCE ENQUIRY OF USER WITH USING TO BANK ACCOUNT NUMBER"
			)
	@ApiResponse(
			responseCode = "200",
			description = "HTTP STATUS 200 SUCCESS"
			)
	public BankResponse balanceEqnuiry(@RequestBody EnquiryRequest enquiryRequest) {
		return userService.balanceEnquiry(enquiryRequest);
	}
	
	@GetMapping("nameEnquiry")
	@Operation(
			summary = "NAME ENQUIRY OF USER BANK ACCOUNT",
			description = "NAME ENQUIRY OF USER BANK ACCOUNT"
			)
	@ApiResponse(
			responseCode = "200",
			description = "HTTP STATUS 201 SUCCESS"
			)
	public String nameEnquiry(@RequestBody EnquiryRequest enquiryRequest) {
		return userService.nameEnquiry(enquiryRequest);
	}
	
	@PostMapping("credit")
	@Operation(
			summary = "CREDITING AMOUNT",
			description = "CREDITING  AMOUNT IN USER BANK ACCOUNT"
			)
	@ApiResponse(
			responseCode = "200",
			description = "HTTP STATUS 200 SUCCESS"
			)
	public BankResponse creditAmountRequest(@RequestBody CreditRequest creditRequest) {
		return userService.creditAmount(creditRequest);
	}
	
	@PostMapping("debit")
	@Operation(
			summary = "DEBITING AMOUNT",
			description = "DEBITING AMOUNT IN USER BANK ACCOUNT"
			)
	@ApiResponse(
			responseCode = "200",
			description = "HTTP STATUS 200 SUCCESS"
			)
	public BankResponse debitAmountRequest(@RequestBody DebitRequest debitRequest) {
		return userService.debitAmount(debitRequest);
	}
	
	@PostMapping("transfer")
	@Operation(
			summary = "TRANSFER REQUEST",
			description = "TRANSFER AMOUNT TO DESTINATION ACCOUNT NUMBER FROM USER BANK ACCOUNT NUMBER"
			)
	@ApiResponse(
			responseCode = "200",
			description = "HTTP STATUS 200 SUCCESS"
			)
	public BankResponse transferRequestAmountToDestinationAccountNumber(@RequestBody TransferAmountRequest transferAmountRequest) {
		return userService.transferAmountRequest(transferAmountRequest);
	}
}