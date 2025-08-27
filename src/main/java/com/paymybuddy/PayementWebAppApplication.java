package com.paymybuddy;

import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class PayementWebAppApplication implements CommandLineRunner {

	@Autowired
	UserService userService;
	@Autowired
	AccountService accountService;
	@Autowired
	Scanner scanner;

	public static void main(String[] args) {
		SpringApplication.run(PayementWebAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

//		while (true) {
//
//			System.out.println("What is the user to delete ? enter its Id or 99 to exit");
//
//			int userId = scanner.nextInt();
//
//			if (userId != 99) {
//				userService.deleteUserById(userId);
//
//			} else {
//				break;
//			}
//		}


	}
}
