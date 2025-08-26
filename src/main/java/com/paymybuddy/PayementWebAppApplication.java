package com.paymybuddy;

import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PayementWebAppApplication implements CommandLineRunner {

	@Autowired
	UserService userService;
	@Autowired
	AccountService accountService;

	public static void main(String[] args) {
		SpringApplication.run(PayementWebAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
