package com.bank.dto;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfo {

	@Schema(
			name="USER Account-NAME"
	)
	private String accountName;
	@Schema(
			name="USER ACCOUNT-BALANCE"
	)
	private BigDecimal accountBalance;
	@Schema(
			name="USER ACCOUNT-NUMBER"
	)
	private String accountNumber;
	
}

