package com.bank.service.EmailService;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.bank.utlis.EmailDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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

	@Override
	public void sendEmailWithAttachment(EmailDetails emailDetails) {
		MimeMessage mimeMessage= javaMailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper;
			try {
				mimeMessageHelper=new MimeMessageHelper(mimeMessage,true);
				mimeMessageHelper.setFrom(senderEmail);
				mimeMessageHelper.setTo(emailDetails.getRecipient());
				mimeMessageHelper.setText(emailDetails.getMessageBody());
				mimeMessageHelper.setSubject(emailDetails.getSubject());
				FileSystemResource file = new FileSystemResource(new File(emailDetails.getAttachment()));
				mimeMessageHelper.addAttachment(file.getFilename(), file);
				
				javaMailSender.send(mimeMessage);
				
				log.info(file.getFilename()+"has been sent to user with email"+emailDetails.getRecipient());
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
		}
	}

