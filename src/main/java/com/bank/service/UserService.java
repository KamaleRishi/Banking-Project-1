package com.bank.service;

import com.bank.Response.BankResponse;
import com.bank.dto.CreditRequest;
import com.bank.dto.DebitRequest;
import com.bank.dto.EnquiryRequest;
import com.bank.dto.LoginDto;
import com.bank.dto.TransferAmountRequest;
import com.bank.dto.UserRequest;

public interface UserService {

	BankResponse createAccount(UserRequest userRequest);
	
	BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
	
	String nameEnquiry(EnquiryRequest enquiryRequest);
	
	BankResponse creditAmount(CreditRequest creditRequest);
	
	BankResponse debitAmount(DebitRequest debitRequest);
	
	BankResponse transferAmountRequest(TransferAmountRequest transferAmountRequest);
	
	BankResponse login(LoginDto loginDto);
}
