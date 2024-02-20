package com.bank.service;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bank.Response.BankResponse;
import com.bank.dto.AccountInfo;
import com.bank.dto.CreditRequest;
import com.bank.dto.DebitRequest;
import com.bank.dto.EnquiryRequest;
import com.bank.dto.TransactionDto;
import com.bank.dto.TransferAmountRequest;
import com.bank.dto.UserRequest;
import com.bank.entities.User;
import com.bank.repository.UserRepository;
import com.bank.service.EmailService.EmailService;
import com.bank.utlis.AccountUtils;
import com.bank.utlis.EmailDetails;
import com.bank.utlis.StatusCode;

@Service
public class UserServiceImpl  implements UserService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Override
	public BankResponse createAccount(UserRequest userRequest) {
	    /**creating an account- saving new bank user into the db
		 *Check if user already has an account
		 */
		if(userRepository.existsByEmail(userRequest.getEmail())) {
			return BankResponse.builder()
					.responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
					.responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
					.accountInfo(null)
					.build();
		}
		
		User newUser = User.builder()
				.firstName(userRequest.getFirstName())
				.lastName(userRequest.getLastName())
				.otherName(userRequest.getOtherName())
				.gender(userRequest.getGender())
				.address(userRequest.getAddress())
				.stateOfOrigin(userRequest.getStateOfOrigin())
				.accountNumber(AccountUtils.generateAccountNumber())
				.accountBalance(BigDecimal.ZERO)
				.email(userRequest.getEmail())
				.phoneNumber(userRequest.getPhoneNumber())
				.alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
				.status("ACTIVE")
				.build();
		
		User savedUser = userRepository.save(newUser);		
		
		EmailDetails emailDetails = EmailDetails.builder()
				.recipient(savedUser.getEmail())
				.subject("HDFC BANK - ACCOUNT CREATION")
				.messageBody("Congratulations: Your Account has been succesfully created.\nYour Account Details :\n"
						+"Account Name: "+savedUser.getFirstName()+ " "+savedUser.getLastName()+ " "+savedUser.getOtherName()+"\nAccount Number: "+savedUser.getAccountNumber()
						+"\nAccount Balance: "+savedUser.getAccountBalance()
						)
				.build();
		
		emailService.sendEmailAlert(emailDetails);
		
		return BankResponse.builder()
				.responseCode(AccountUtils.ACCOUNT_CREATED_SUCCESS)
				.responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
				.accountInfo(AccountInfo.builder()
						.accountBalance(savedUser.getAccountBalance()) 
						.accountNumber(savedUser.getAccountNumber())
						.accountName(savedUser.getFirstName()+ " "+savedUser.getLastName()+ " "+savedUser.getOtherName())
						.build())
				.build();
	}

	//balance Eniquiry, name Enquiry, credit amount, debit amount, transfer amount
	
	@Override
	public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
		//check if the provided account number exists in the db
		boolean isAccountExists = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
		if(!isAccountExists) {
			return BankResponse.builder()
					.responseCode(AccountUtils.ACCOUNT_NON_EXISTS_CODE)
					.responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
					.accountInfo(null)
					.build();
		}
		User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
		
		return BankResponse.builder()
				.responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
				.responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
				.accountInfo(AccountInfo.builder()
						.accountName(foundUser.getFirstName()+foundUser.getLastName()+foundUser.getOtherName())
						.accountNumber(foundUser.getAccountNumber())
						.accountBalance(foundUser.getAccountBalance())
						.build())
				.build();
	}

	@Override
	public String nameEnquiry(EnquiryRequest enquiryRequest) {
		boolean isAccountExists = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
		if(!isAccountExists) {
			return AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE;
		}
		User findByAccountNumber = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
		
		return findByAccountNumber.getFirstName()+ " "+findByAccountNumber.getLastName()+ " "+findByAccountNumber.getOtherName();
	}

	@Override
	public BankResponse creditAmount(CreditRequest creditRequest) {
		//checking if the accountExists or not 
		boolean isAccountExists = userRepository.existsByAccountNumber(creditRequest.getAccountNumber());
		if(!isAccountExists) {
			return BankResponse.builder()
					.responseCode(AccountUtils.ACCOUNT_NON_EXISTS_CODE)
					.responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
					.accountInfo(null)
					.build();
		}
		User userToCredit = userRepository.findByAccountNumber(creditRequest.getAccountNumber());
		userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(creditRequest.getAmount()));
		userRepository.save(userToCredit);

		TransactionDto transactionDTO = TransactionDto.builder()
				.accountNumber(userToCredit.getAccountNumber())
			.transactionType("CREDIT")
			.amount(creditRequest.getAmount())
					.build();

		
		return BankResponse.builder()
				.responseCode(AccountUtils.AMOUNT_CREDITED_SUCESS_CODE)
				.responseMessage(AccountUtils.AMOUNT_CREDITED_SUCCESS_MESSAGE)
				.accountInfo(AccountInfo.builder()
						.accountName(userToCredit.getFirstName()+" "+ userToCredit.getLastName()+" "+userToCredit.getOtherName())
						.accountNumber(userToCredit.getAccountNumber())
						.accountBalance(userToCredit.getAccountBalance())
						.build())
				.build();
	}

	@Override
	public BankResponse debitAmount(DebitRequest debitRequest) {
		//check if the given account is exists 
		boolean isAccountExists = userRepository.existsByAccountNumber(debitRequest.getAccountNumber());
		if(!isAccountExists) {
			return BankResponse.builder()
					.responseCode(AccountUtils.ACCOUNT_NON_EXISTS_CODE)
					.responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
					.accountInfo(null)
					.build();
		}
		//check if the request amount is intend to withdraw is available or not with the given request transaction
		
		User debitAmountRequest = userRepository.findByAccountNumber(debitRequest.getAccountNumber());
		BigInteger availableBalance = debitAmountRequest.getAccountBalance().toBigInteger();
		BigInteger debitAmount = debitRequest.getAmount().toBigInteger();
        if(availableBalance.intValue()< debitAmount.intValue()) {
        	return BankResponse.builder()
        			.responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
        			.responseMessage(AccountUtils.INSUFFICIENT_BALANECE_MESSAGE)
        			.accountInfo(null)
        			.build();
        }else {
        	debitAmountRequest.setAccountBalance(debitAmountRequest.getAccountBalance().subtract(debitRequest.getAmount()));
        	userRepository.save(debitAmountRequest);
        	return BankResponse.builder()
        			.responseCode(AccountUtils.AMOUNT_DEBIT_REQUEST_SUCCESS_CODE)
        			.responseMessage(AccountUtils.AMOUNT_DEBIT_REQUEST_SUCCESS_MESSAGE)
        			.accountInfo(AccountInfo.builder()
        					.accountName(debitAmountRequest.getFirstName()+ " "+debitAmountRequest.getLastName()+" "+debitAmountRequest.getOtherName())
        					.accountNumber(debitAmountRequest.getAccountNumber())
        					.accountBalance(debitAmountRequest.getAccountBalance())
        					.build())
        			.build();
        }
		
	}

	@Override
	public BankResponse transferAmountRequest(TransferAmountRequest transferAmountRequest) {
		//get the account to debit
		//check if the amount of the current user is not is have sufficient balance to transfer
		//debit amount
		//get the account to credit
		//credited amount
		boolean isDestinationAccountNumber = userRepository.existsByAccountNumber(transferAmountRequest.getDestinationAccountNumber());
		if(!isDestinationAccountNumber) {
			return BankResponse.builder()
					.responseCode(AccountUtils.ACCOUNT_NON_EXISTS_CODE)
					.responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
					.accountInfo(null)
					.build();
		}
		
		User sourceAccountNumber = userRepository.findByAccountNumber(transferAmountRequest.getSourceAccountNumber());
		if(transferAmountRequest.getAmount().compareTo(sourceAccountNumber.getAccountBalance())>0) {
			return BankResponse.builder()
					.responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
        			.responseMessage(AccountUtils.INSUFFICIENT_BALANECE_MESSAGE)
        			.accountInfo(null)
					.build();
		}
		
		sourceAccountNumber.setAccountBalance(sourceAccountNumber.getAccountBalance().subtract(transferAmountRequest.getAmount()));
		String sourceAccountUserName= sourceAccountNumber.getFirstName()+" "+sourceAccountNumber.getLastName()+" "+sourceAccountNumber.getOtherName();
		userRepository.save(sourceAccountNumber);
		
		EmailDetails debitAlert= EmailDetails.builder()
				.subject("DEBIT ALERT")
				.recipient(sourceAccountNumber.getEmail())
				.messageBody("The sum of "+transferAmountRequest.getAmount()+ " has been deducted from your account!\nYour current balance is "+sourceAccountNumber.getAccountBalance())
				.build();
		emailService.sendEmailAlert(debitAlert);
		
		User destinationAccountNumber = userRepository.findByAccountNumber(transferAmountRequest.getDestinationAccountNumber());
		destinationAccountNumber.setAccountBalance(destinationAccountNumber.getAccountBalance().add(transferAmountRequest.getAmount()));
		String recipientUserName=destinationAccountNumber.getFirstName()+" "+destinationAccountNumber.getLastName()+" "+destinationAccountNumber.getOtherName();
		userRepository.save(destinationAccountNumber);
		
		EmailDetails creditAlert= EmailDetails.builder()
				.subject("HDFC BANK-ALERT")
				.subject("CREDIT-AMOUNT")
				.recipient(destinationAccountNumber.getEmail())
				.messageBody("The sum of "+transferAmountRequest.getAmount()+ " has been sent to your account from: "+sourceAccountUserName+ "\nYour current balance is: "+destinationAccountNumber.getAccountBalance())
				.build();
		emailService.sendEmailAlert(creditAlert);
		
		return BankResponse.builder()
				.responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
				.responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
				.accountInfo(AccountInfo.builder()
						.accountBalance(sourceAccountNumber.getAccountBalance())
						.accountName(sourceAccountNumber.getFirstName()+ " "+sourceAccountNumber.getLastName()+ " "+sourceAccountNumber.getOtherName())
						.accountNumber(sourceAccountNumber.getAccountNumber())
						.build())
				.build();
	}
      

}
