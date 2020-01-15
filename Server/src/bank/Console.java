package bank;

import java.util.ArrayList;
import java.util.Scanner;
import org.apache.log4j.*;

import bank.DBAccess.*;
import bank.util.*;

public class Console {
	static Logger log = Logger.getLogger(Console.class);
	final static String bankName = "Galactic Imperial Credit Union";
	final static String moraleMessage = "Remember, we own you!";
	// Passwords should be hashed serverside.

	static DBInterface Handler;
	static Scanner scanner;
	static User CurrentUser;

	public static void main(String[] args) {

		Handler = new DBHandler();
		scanner = new Scanner(System.in);

		// "Register USERNAME PASSWORD" creates an account, if USERNAME isn't taken.
		// "Manage USERNAME PASSWORD" checks if USERNAME/PasswordHash matches any in
		// database, and enables admin tools.
		// "Login USERNAME PASSWORD"
		if (args.length == 0) {
			defaultConsole();
		} else if (args[0].equals("Register") && args.length >= 3) {
			registerConsole(args[1], args[2]);
		} else if (args[0].equals("Manage") && args.length >= 3) {
			managementConsole(args[1], args[2]);
		} else if (args[0].equals("Login") && args.length >= 3) {
			customerConsole(args[1], args[2]);
		} else {
			System.out.println("Input not recognized.");
			defaultConsole();
		}

		return;
	}

	private static void customerConsole(String Username, String Password) {

		try {
		Handler.Login(Username, Password, false); 
		CurrentUser = Handler.getUser(Username);
		} catch (DBAccessException e) {
			System.out.print(e);
			return;
		}

		System.out.println("Login verified. Hello, " + CurrentUser.getUsername() + "!");
		System.out.println("Your balance is " + CurrentUser.getBalance());
		ArrayList<Transfer> transfers = Handler.getPendingTransactions();

		if (transfers.size() > 0) {
			System.out.println("You have " + transfers.size() + " pending money transfers!");
		}

		String input = "";
		while (true) {
			System.out.println("Commands: Withdraw, Deposit, Send, Quit");
			input = scanner.next();

			if (input.equals("Withdraw")) {
				BalanceConsole(false);
				continue;
			} else if (input.equals("Deposit")) {
				BalanceConsole(true);
				continue;
			} else if (input.equals("Send")) {
				SendConsole();
				continue;
			} else if (input.equals("Quit")) {
				break;
			} else {
				System.out.println("Invalid command.");
			}
		}

	}

	private static void BalanceConsole(boolean Deposit) {
		System.out.println("Your balance is " + Handler.getUser().getBalance());
		System.out.println("Please enter amount:");
	

		int value = scanner.nextInt();
		if (Deposit == true && value > 0) {
			System.out.println("Depositing " + value);

			Handler.Deposit(value);
			//CurrentUser.getPendingTransfers().add(NewTransfer);
			//DISABLED: Let DB handle actual changing of money
			//CurrentUser.setBalance(CurrentUser.getBalance() + value);
		} else {
			if (value > 0 && value < Handler.getUser().getBalance()) {
				System.out.println("Withdrawing " + value);
				
				Handler.Deposit(-value);
				//DISABLED: Let DB handle actual changing of money
				//CurrentUser.setBalance(CurrentUser.getBalance() - value);
			} else {
				System.out.println("INVALID AMOUNT.");
			}
		}
		// Add a verification function here later
		System.out.println("Your new balance is " + Handler.getUser().getBalance());
	}

	private static void SendConsole() {

		System.out.println("Your balance is " + Handler.getUser().getBalance());
		System.out.println("Please enter name of person to send money to:");

		String Targetname = scanner.nextLine();
			System.out.println("Please enter amount of money:");
			int sendvalue = scanner.nextInt();

			// Block invalid transfers
			if (sendvalue > Handler.getUser().getBalance()) {
				System.out.println("Invalid amount specified.");
				return;
			}
			
			Handler.Transfer(sendvalue, Targetname);
			System.out.println("Transfer Registered!");
		
	}

	private static void defaultConsole() {
		System.out.println("Appropriate commands are...");
		System.out.println("Register [Username] [Password],");
		System.out.println("Manage [Username] [Password],");
		System.out.println("Login [Username] [Password]");
		return;
	}

	private static void managementConsole(String Username, String Password) {

		try {
		Handler.Login(Username, Password, true);
		} catch (DBAccessException e) {
			System.out.println(e);
			return;
		}

		System.out.println("Login verified. Hello, " + Handler.getUser().getUsername() + "!");
		System.out.println(moraleMessage);

		String input = "";
		while (true) {
			System.out.println("Commands: User, Log, Quit");
			input = scanner.nextLine();

			if (input.equals("User")) {
				UserViewer();
				continue;
			}
			if (input.equals("Log")) {
				LogViewer();
				continue;
			}
			if (input.equals("Quit")) {
				break;
			}
			System.out.println("Invalid command.");

		}
	}

	private static void UserViewer() {
		System.out.println("Please enter name of user to view:");

		String value = scanner.nextLine();
		User TargetUser = Handler.getUser(value);
		if (TargetUser == null) {
			System.out.println("User not found.");
			return;
		} else {
			System.out.println(TargetUser);
			if (TargetUser.getStatus() == 0) {
				System.out.println("This user can be approved. Would you like to? Y/N");
			} else if (TargetUser.getStatus() == 1) {
				System.out.println("This user can be promoted to employee. Would you like to? Y/N");

			} else {
				return;
			}

			while (true) {
				value = scanner.nextLine();
				if (value.equals("Y") || value.equals("y")) {
					Handler.promoteUser(TargetUser.getUsername());
					System.out.println("User status promoted.");
					return;
				} else if (value.equals("N") || value.equals("N")) {
					return;
				} else {

				}
			}

		}

	}

	private static void LogViewer() {
		ArrayList<Transfer> Logs = Handler.getLogs(null);

		System.out.println("Displaying all transactions");
		System.out.print(Logs);
	}

	private static void registerConsole(String Username, String Password) {
		Handler.RegisterUser(Username, Password);
		System.out.println("New User " + Username + " created!");
		System.out.println("Please wait for approval.");
		return;
	}

}
