package com.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@SpringBootApplication
@OpenAPIDefinition(info=
                       @Info(title="BANKING-SPRING-BOOT-APPLICATION",
                       description = "BACKEND REST API OF HDFC-BANK-APPLICATION",
                       version = "v1.0",
                       contact=@Contact(
                    		   name="KAMALE SREEKANTH",
                               email="kamalerishi99@gmail.com",
                               url="https://github.com/KamaleRishi/BANKING-PROJECT"),
                               license =  @License(
                            		   name="KAMALE-RISHI",
                            		   url="https://github.com/KamaleRishi/BANKING-PROJECT"
                            		   )
                    		   ),
                          externalDocs = @ExternalDocumentation(
                        		  description = "JAVA HDFC-CLONE-BANKING-PROJECT",
                        		  url="https://github.com/KamaleRishi/BANKING-PROJECT"
                        		  ))
public class BankingProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankingProjectApplication.class, args);
	}

}
