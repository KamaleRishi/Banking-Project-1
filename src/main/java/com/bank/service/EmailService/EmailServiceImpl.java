package com.bank.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.bank.utlis.EmailDetails;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService{

	@Autowired
	private JavaMailSender javaMailSender;
	
	@Value("${spring.mail.username}")
	private String senderEmail;
	
	@Override
	public void sendEmailAlert(EmailDetails emailDetails) {
		 SimpleMailMessage mailMessage = new SimpleMailMessage();
		 mailMessage.setFrom(senderEmail);
		 mailMessage.setTo(emailDetails.getRecipient());
		 mailMessage.setSubject(emailDetails.getSubject());
		 mailMessage.setText(emailDetails.getMessageBody());
		 javaMailSender.send(mailMessage);
	}

}
