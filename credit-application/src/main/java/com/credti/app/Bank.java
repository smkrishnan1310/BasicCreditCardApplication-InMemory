package com.credti.app;

import java.util.Date;
import java.util.Scanner;

import com.credti.CreditCardAgency;
import com.credti.common.InstanceFactory;
import com.credti.common.Logger;
import com.credti.constants.CardStatus;
import com.credti.domain.CardDetails;
import com.credti.domain.CreditCard;
import com.credti.domain.CustomerDetails;
import com.credti.processor.EOMPProcessor;
import com.credti.service.AccountService;
import com.credti.service.PaymentService;
import com.credti.service.TransactionService;

/**
 * Please read below to understand the way it was developed and missing
 * functionalities for better one
 * 
 * Whole system is designed with following assumption that it has to be fully
 * based on Java (In-Memory) and not using any additional frameworks/ DBs and
 * functionalities are restricted to asked in question
 * 
 * This (Bank.java) is to simulate end usage application like UI app where
 * customer/banker has to use. Instead of test cases wrote this to test manually
 * by accepting inputs from USER as to understand the functionalities
 * 
 * Also, this is just for basic understanding of the app and also assuming as
 * this is for interview purpose i didn't added more negative cases in this app
 * (Bank.java) and in negative cases, closed the app abruptly or simply ignoring
 * those inputs and (Bank.java) may not be cleaner
 * 
 * For eg./ If we provide invalid option other than [1-8] closing the app or if
 * we provide input more than limit then simply will display operation failed.
 * If we input invalid Card ID will simply ignoring it and re-asking inputs
 * 
 * To Simulate EOMP Processor functionality manually, instead of waiting for a
 * month to calculate, in option we can use number 7 which will forcefully
 * calculate interest, late fee and other parameters
 * 
 * Also, for note, most of the method in this system are not thread safe except
 * EOMP Processor as it is running alone in fixed interval. Concurrency has to
 * be handled at the end application. For eg. withdraw, payment all those should
 * be handled in application. Here (Bank.java) is the end application which
 * handled those concurrency issues. Here Obtaining lock on card level so no two
 * operations will not happen concurrently for same card but will happen for
 * different cards. We can use locks as well for better performance for code
 * simplicity used synchronized blocks
 * 
 * Logger.java we can design in more generic way so that can use for multiple
 * ways but as it is out of ques i restricted to current usage and generated log
 * file which are logged ones and file can be found at pom file location
 * 
 * Also, we can maintain transaction log, audit trails, providing functionality
 * by user roles (in Java we can achieve it by thread names which i did in
 * earlier task) and other functionalities like handling negative cases grace
 * fully with proper design patterns for better designing
 * 
 * For calculations assuming, if we do cash withdraw which comes under credit
 * usage as well. so credit usage always include cash withdraw for eg. if cash
 * withdraw is Rs.50 and swiped (credit usage) is for Rs.100 then credit usage
 * will show Rs. 150(total usage inspite of way it utilized) and cash usage show
 * Rs. 50
 * 
 * 
 */
public class Bank implements CreditCardAgency {

	private static final String MSG = "Welcome!!" + System.lineSeparator() + "Press \n1. Apply card    2. Pay Due"
			+ System.lineSeparator() + "3. Close Card    4. Withdraw" + System.lineSeparator()
			+ "5. Swipe    6. Show Complete Card Details" + System.lineSeparator()
			+ "8. Exit    7. Force EOMP Calculation to test" + System.lineSeparator();

	private static Scanner sc = new Scanner(System.in);

	private final Object createLock = new Object();

	public static void main(String args[]) {
		Logger.log("Launching bank application.....");
		Bank b = new Bank();
		try {
			int opt = 0;
			while (opt != 8) {
				System.out.println(MSG);
				try {
					opt = sc.nextInt();
				} catch (Exception ex) {
					System.out.println("Invalid Input!! Press Valid num (1-7). Now shutting down....");

				}
				switch (opt) {
				case 1:
					b.applyCreditCard();
					break;
				case 2:
					b.payDue();
					break;
				case 3:
					b.closeCreditCard();
					break;
				case 4:
					b.withDraw();
					break;
				case 5:
					b.swipe();
					break;
				case 6:
					b.showCompleteCardDetails();
					break;
				case 7:
					b.forceEOMP();
					break;
				default:
					opt = 8;
					break;
				}
			}
		} finally {
			System.out.println("Thank U!! Closing bank application...");
			sc.close();
			EOMPProcessor.shutDownAllServices();
		}
	}

	@Override
	public Long applyCreditCard() {
		System.out.println("Name: ");
		String name = sc.next();
		System.out.println("Phone: ");
		String phone = sc.next();
		System.out.println("City: ");
		String city = sc.next();
		Date upto = new Date();
		upto.setYear(upto.getYear() + 4);
		synchronized (createLock) {
			CardDetails cd = new CardDetails(new Date(), upto, 10000, 2000, CardStatus.ACTIVE);
			CustomerDetails cust = new CustomerDetails(name, city, phone);
			try {
				AccountService accService = (AccountService) InstanceFactory.get(AccountService.class);
				long id = accService.createAcccount(cd, cust);
				Logger.log("New Credit card created with ID: " + id);
				return id;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		Logger.log("New Credit card creation failed");
		return null;
	}

	@Override
	public boolean payDue() {
		System.out.println("Enter ID: ");
		long id = sc.nextLong();
		System.out.println("Amount: ");
		double amt = sc.nextDouble();
		try {
			PaymentService ps = (PaymentService) InstanceFactory.get(PaymentService.class);
			AccountService accService = (AccountService) InstanceFactory.get(AccountService.class);
			CreditCard card = accService.getCreditCard(id);
			synchronized (card) {
				boolean res = ps.payDue(card, amt);
				if (res) {
					Logger.log("Payment success for ID: " + id);
					return res;
				}
				return res;
			}
		} catch (Exception ex) {
		}
		Logger.log("Payment failed for ID: " + id);
		return false;
	}

	@Override
	public boolean closeCreditCard() {
		try {
			System.out.println("Enter ID: ");
			final long id = sc.nextLong();
			Logger.log("Requested for closing card ID: " + id);
			AccountService accService = (AccountService) InstanceFactory.get(AccountService.class);
			CreditCard card = accService.getCreditCard(id);
			if (accService.closeCard(card)) {
				Logger.log("Card closed successfully ID: " + id);
				return true;
			} else
				Logger.log("Card not eligible to close ID: " + id);
		} catch (Exception ex) {
		}
		return false;
	}

	public void withDraw() {
		System.out.println("ID: ");
		long id = sc.nextLong();
		System.out.println("Amount: ");
		double amt = sc.nextDouble();
		try {
			AccountService accService = (AccountService) InstanceFactory.get(AccountService.class);
			CreditCard card = accService.getCreditCard(id);
			synchronized (card) {
				TransactionService ts = (TransactionService) InstanceFactory.get(TransactionService.class);
				boolean res = ts.withdraw(card, amt);
				if (res)
					Logger.log("Withdraw success for ID: " + id);
				else
					Logger.log("Withdraw Failed for ID: " + id);
			}
		} catch (Exception e) {
		}
	}

	public void swipe() {
		System.out.println("ID: ");
		long id = sc.nextLong();
		System.out.println("Amount: ");
		double amt = sc.nextDouble();
		try {
			AccountService accService = (AccountService) InstanceFactory.get(AccountService.class);
			CreditCard card = accService.getCreditCard(id);
			synchronized (card) {
				TransactionService ts = (TransactionService) InstanceFactory.get(TransactionService.class);
				boolean res = ts.swipe(card, amt);
				if (res)
					Logger.log("Swipe success for ID: " + id);
				else
					Logger.log("Swipe Failed for ID: " + id);
			}
		} catch (Exception e) {
		}
	}

	public void forceEOMP() {
		Logger.log("Forced EOMP Calculation started");
		EOMPProcessor eomp = (EOMPProcessor) InstanceFactory.get(EOMPProcessor.class);
		eomp.process();
		Logger.log("Forced EOMP Calculation completed");
	}

	public void showCompleteCardDetails() {
		System.out.println("ID: ");
		long id = sc.nextLong();
		try {
			AccountService accService = (AccountService) InstanceFactory.get(AccountService.class);
			CreditCard card = accService.getCreditCard(id);
			System.out.println(card);
		} catch (Exception e) {
		}
	}
}
