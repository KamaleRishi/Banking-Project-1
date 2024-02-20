package com.bank.Response;

import com.bank.dto.AccountInfo;
import com.bank.utlis.StatusCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankResponse {

	private String responseCode;
	
	private String responseMessage;
	
	private AccountInfo accountInfo;
	
}
