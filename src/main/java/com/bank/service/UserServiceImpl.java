package com.bank.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.bank.Response.BankResponse;
import com.bank.config.JwtTokenProvider;
import com.bank.dto.AccountInfo;
import com.bank.dto.CreditRequest;
import com.bank.dto.DebitRequest;
import com.bank.dto.EnquiryRequest;
import com.bank.dto.LoginDto;
import com.bank.dto.TransactionDto;
import com.bank.dto.TransferAmountRequest;
import com.bank.dto.UserRequest;
import com.bank.entities.User;
import com.bank.repository.UserRepository;
import com.bank.service.EmailService.EmailService;
import com.bank.utlis.AccountUtils;
import com.bank.utlis.EmailDetails;
import com.bank.utlis.Role;
import com.bank.utlis.TransactionType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl  implements UserService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
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
				.password(passwordEncoder.encode(userRequest.getPassword()))
				.phoneNumber(userRequest.getPhoneNumber())
				.alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
				.status("ACTIVE")
				.role(Role.valueOf("ROLE_ADMIN"))
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
	
	public BankResponse login(LoginDto loginDto) {
		Authentication authentication=null;
		authentication= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
		EmailDetails emailDetails = EmailDetails.builder()
				.subject("your logged in..!")
				.recipient(loginDto.getEmail())
				.messageBody("You logged into your account. If you didn't intiate this request, please contact HDFC-BANK")
				.build();
		emailService.sendEmailAlert(emailDetails);
		
		return BankResponse.builder()
				.responseCode("Login SUCESS")
				.responseMessage(jwtTokenProvider.generateToken(authentication))
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
		
//		TransactionDto transactionDTO = TransactionDto.builder()
//				.accountNumber(userToCredit.getAccountNumber())
//				.transactionType("CREDIT")
//				.amount(creditRequest.getAmount())
//				build()

		EmailDetails creditAlert= EmailDetails.builder()
				.subject("CREDIT ALERT")
				.recipient(userToCredit.getEmail())
				.messageBody("The sum of "+creditRequest.getAmount()+ " has been Credited to your account!\nYour current balance is "+userToCredit.getAccountBalance())
				.build();
		emailService.sendEmailAlert(creditAlert);
		
		TransactionDto transactionDto = TransactionDto.builder()
				.accountNumber(userToCredit.getAccountNumber())
				.transactionType(TransactionType.CREDIT)
				.amount(creditRequest.getAmount())
				.build();
		transactionService.saveTransaction(transactionDto);
		
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
		
		User userTodebit = userRepository.findByAccountNumber(debitRequest.getAccountNumber());
		BigInteger availableBalance = userTodebit.getAccountBalance().toBigInteger();
		BigInteger debitAmount = debitRequest.getAmount().toBigInteger();
		
        if(availableBalance.intValue()< debitAmount.intValue()) {
        	return BankResponse.builder()
        			.responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
        			.responseMessage(AccountUtils.INSUFFICIENT_BALANECE_MESSAGE)
        			.accountInfo(null)
        			.build();
        	
        }else {
        	userTodebit.setAccountBalance(userTodebit.getAccountBalance().subtract(debitRequest.getAmount()));
        	userRepository.save(userTodebit);
        	
        	EmailDetails debitAlert= EmailDetails.builder()
    				.subject("DEBIT ALERT")
    				.recipient(userTodebit.getEmail())
    				.messageBody("The sum of "+debitRequest.getAmount()+ " has been debited to your account!\nYour current balance is "+userTodebit.getAccountBalance())
    				.build();
    		emailService.sendEmailAlert(debitAlert);
        	
        	TransactionDto transactionDto = TransactionDto.builder()
    				.accountNumber(userTodebit.getAccountNumber())
    				.transactionType(TransactionType.DEBIT)
    				.amount(debitRequest.getAmount())
    				.build();
    		transactionService.saveTransaction(transactionDto);
        	
        	return BankResponse.builder()
        			.responseCode(AccountUtils.AMOUNT_DEBIT_REQUEST_SUCCESS_CODE)
        			.responseMessage(AccountUtils.AMOUNT_DEBIT_REQUEST_SUCCESS_MESSAGE)
        			.accountInfo(AccountInfo.builder()
        					.accountName(userTodebit.getFirstName()+ " "+userTodebit.getLastName()+" "+userTodebit.getOtherName())
        					.accountNumber(userTodebit.getAccountNumber())
        					.accountBalance(userTodebit.getAccountBalance())
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
		
		TransactionDto transactionDto = TransactionDto.builder()
				.accountNumber(destinationAccountNumber.getAccountNumber())
				.transactionType(TransactionType.CREDIT)
				.amount(transferAmountRequest.getAmount())
				.build();
		transactionService.saveTransaction(transactionDto);
		
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
