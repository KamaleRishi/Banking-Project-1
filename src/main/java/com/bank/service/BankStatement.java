package com.bank.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.bank.entities.Transaction;
import com.bank.entities.User;
import com.bank.repository.TransactionsRepository;
import com.bank.repository.UserRepository;
import com.bank.service.EmailService.EmailService;
import com.bank.utlis.EmailDetails;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
@AllArgsConstructor
public class BankStatement {

	/**
	 * retrieve list of transactions within a data range given an account number
	 * generate a pdf file of transactions
	 * send the file via email
	 */
	@Autowired
	private TransactionsRepository transactionsRepository;
	
	private static final String FILE="C:\\Users\\Sreekanth\\Downloads\\BankStatement\\Statement.pdf";
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;
	
	public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws FileNotFoundException, DocumentException{
		LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
		LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
		List<Transaction> transactionList = transactionsRepository.findAll().stream().filter(transaction-> transaction.getAccountNumber().equals(accountNumber))
		.filter(transaction-> transaction.getCreatedAt().isEqual(start)).filter(transaction->transaction.getCreatedAt().isEqual(end)).toList();

		User user = userRepository.findByAccountNumber(accountNumber);
		String customerName =user.getFirstName() + user.getLastName()+user.getOtherName();
		
		Rectangle statementSize = new Rectangle(PageSize.A4);
		Document document = new Document(statementSize);
		log.info("Setting size of document");
		FileOutputStream outputStream = new FileOutputStream(FILE);
		PdfWriter.getInstance(document, outputStream);
		document.open();
		
		PdfPTable bankPdfInfoTable = new PdfPTable(1);
		PdfPCell bankName = new PdfPCell(new Phrase("HDFC-BANK ## PERSONAL-BANKING-NET-BANKING"));
		bankName.setBorder(0);
		bankName.setBackgroundColor(BaseColor.CYAN);
		bankName.setPadding(20f);
		
		PdfPCell bankAddress = new PdfPCell(new Phrase("1/1, Old, 118, Whitefield Main Rd, opposite Reliance Fresh, Bengaluru, Karnataka 560066"));
		bankAddress.setBorder(0);
		bankPdfInfoTable.addCell(bankName);
		bankPdfInfoTable.addCell(bankAddress);
		
		PdfPTable statementInfo = new PdfPTable(2);
		PdfPCell customInfo= new PdfPCell(new Phrase("Start Date: "+startDate));
		customInfo.setBorder(0);
		PdfPCell statement = new PdfPCell(new Phrase("HDFC-BANK STATEMENT OF YOUR ACCOUNT"));
		statement.setBorder(0);
		PdfPCell stopDate = new PdfPCell(new Phrase("End Date: "+endDate));
		stopDate.setBorder(0);
		PdfPCell name = new PdfPCell(new Phrase("Customer Name: "+customerName));
		name.setBorder(0);
		PdfPCell space = new PdfPCell();
		PdfPCell address = new PdfPCell(new Phrase("Customer Address: "+user.getAddress()));
		address.setBorder(0);
		
		PdfPTable transactionTable = new PdfPTable(4);
		PdfPCell date = new PdfPCell(new Phrase("DATE"));
		date.setBackgroundColor(BaseColor.CYAN);
		date.setBorder(0);
		PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
		transactionType.setBackgroundColor(BaseColor.CYAN);
		transactionType.setBorder(0);
		PdfPCell transactionAmount = new PdfPCell(new Phrase("TRANSACTION AMOUNT"));
		transactionAmount.setBackgroundColor(BaseColor.CYAN);
		transactionAmount.setBorder(0);
		PdfPCell status = new PdfPCell(new Phrase("STATUS"));
		status.setBackgroundColor(BaseColor.CYAN);
		status.setBorder(0);
		
		transactionTable.addCell(date);
		transactionTable.addCell(transactionType);
		transactionTable.addCell(transactionAmount);
		transactionTable.addCell(status);
		
		transactionList.forEach(transaction->{
			transactionTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
			transactionTable.addCell(new Phrase(transaction.getTransactionType().toString()));
			transactionTable.addCell(new Phrase(transaction.getAmount().toString()));
			transactionTable.addCell(new Phrase(transaction.getStatus().toString()));
		});
		
		statementInfo.addCell(customInfo);
		statementInfo.addCell(statement);
		statementInfo.addCell(endDate);
		statementInfo.addCell(name);
		statementInfo.addCell(space);
		statementInfo.addCell(address);
		
		document.add(bankPdfInfoTable);
		document.add(statementInfo);
		document.add(transactionTable);
		document.close();
		
		EmailDetails emailDetails = EmailDetails.builder()
				.recipient(user.getEmail())
				.subject("HDFC BANK ACCOUNT STATMENT")
				.messageBody("<h1>Kindly find the attached document requested for your account statement attached!</h1>")
				.attachment(FILE)
				.build();
		emailService.sendEmailWithAttachment(emailDetails);
		
		return transactionList;
	}
	
}
