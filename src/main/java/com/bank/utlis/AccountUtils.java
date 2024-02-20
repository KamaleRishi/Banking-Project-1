package com.bank.utlis;

import java.time.Year;

public class AccountUtils {

	
	public static final String ACCOUNT_EXISTS_CODE="001";
	public static final String ACCOUNT_EXISTS_MESSAGE="This user already has an account Created";
	public static final String ACCOUNT_CREATED_SUCCESS="002";
	public static final String ACCOUNT_CREATION_MESSAGE="Account has been succesfully created!";
	public static final String ACCOUNT_NOT_EXISTS_MESSAGE="User with provided account number don't have Account";
	public static final String ACCOUNT_NON_EXISTS_CODE="003";
	public static final String ACCOUNT_FOUND_CODE ="004";
	public static final String ACCOUNT_FOUND_SUCCESS="Your Account found succesfully";
	public static final String AMOUNT_CREDITED_SUCCESS_MESSAGE="The given amount credited sucessfully";
	public static final String AMOUNT_CREDITED_SUCESS_CODE="005";
	public static final String INSUFFICIENT_BALANCE_CODE="006";
	public static final String INSUFFICIENT_BALANECE_MESSAGE="Insufficient Balance";
	public static final String AMOUNT_DEBIT_REQUEST_SUCCESS_CODE="007";
	public static final String AMOUNT_DEBIT_REQUEST_SUCCESS_MESSAGE="The request amount debited from account Succesfully";
    public static final String TRANSFER_SUCCESSFUL_CODE= "008";
    public static final String TRANSFER_SUCCESSFUL_MESSAGE="The Request Amount has been Transfer to the Account Sucessfully";
	
	public static String generateAccountNumber() {
		
		Year currentYear = Year.now();
		int min =100000000;
		int max =999999999;
		
		//generate a random number between min and max
		int randomNumber =(int) Math.floor(Math.random()*(max-min+1)+min);
		
		// convert the current and randomNumber to Strings then Concatenate
		 
		String year = String.valueOf(currentYear);
		String randNumber = String.valueOf(randomNumber);
		
          return year+randNumber;
	}
	
}
