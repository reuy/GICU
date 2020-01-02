package bank;

import java.util.ArrayList;
import java.util.Scanner;
import org.apache.log4j.*;

public class Console {
	static Logger log = Logger.getLogger(Console.class);
	final static String bankName = "Galactic Imperial Credit Union";
	final static String moraleMessage = "Remember, we own you!";
	// Passwords should be hashed serverside.
	static User CurrentUser;
	static User TargetUser;
	static DBInterface Handler;
	static Scanner scanner;

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

		CurrentUser = Handler.Login(Username, Password, false);
		if (CurrentUser == null) {
			System.out.println("Customer account not found.");
			return;
		}
		if (CurrentUser.getStatus() == 0) {
			System.out.println("Customer account still pending. Contact customer support at 1-800-PICKLES.");
			return;
		}

		System.out.println("Login verified. Hello, " + CurrentUser.getUsername() + "!");
		System.out.println("Your balance is " + CurrentUser.getBalance());
		ArrayList<Transfer> transfers = CurrentUser.pendingTransfers;
		
		if (transfers.size() > 0) {
			System.out.println("You have " + transfers.size() + " pending money transfers!");
		}

		String input = "";
		while (true) {
			System.out.println("Commands: Withdraw, Deposit, Send, Quit");
			input = scanner.nextLine();

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
		System.out.println("Your balance is " + CurrentUser.getBalance());
		System.out.println("Please enter amount:");

		int value = scanner.nextInt();
		if (Deposit == true) {
			System.out.println("Depositing " + value);
			CurrentUser.setBalance(CurrentUser.getBalance() + value);
		} else {
			if (value < CurrentUser.getBalance()) {
				System.out.println("Withdrawing " + value);
				CurrentUser.setBalance(CurrentUser.getBalance() - value);
			} else {
				System.out.println("INVALID AMOUNT.");
			}
		}

			Handler.updateUser(CurrentUser, null);
			// Add a verification function here later
			System.out.println("Your new balance is " + CurrentUser.getBalance());
		
	}

	private static void SendConsole() {
		System.out.println("Thank you for choosing " + bankName);
		System.out.println("Please enter name of person to send money to:");
		
		
		String value = scanner.nextLine();
		TargetUser = Handler.viewUser(value);
		if (TargetUser == null) {
			System.out.println("User not found.");
			return;
		} else {
			System.out.println("Please enter amount of money:");
			int sendvalue = scanner.nextInt();
			
			//Block invalid transfers
			if(sendvalue > CurrentUser.getBalance())
			{System.out.println("Invalid amount specified.");
			return;}
			
			CurrentUser.setBalance(CurrentUser.getBalance() - sendvalue);
			
			Transfer NewTransfer = new Transfer(CurrentUser.getUsername(), TargetUser.getUsername(), sendvalue);
			CurrentUser.getPendingTransfers().add(NewTransfer);
			System.out.println("Transfer Registered!");
			Handler.updateUser(CurrentUser, null);
			}
	}

	private static void defaultConsole() {
		System.out.println("Appropriate commands are...");
		System.out.println("Register [Username] [Password],");
		System.out.println("Manage [Username] [Password],");
		System.out.println("Login [Username] [Password],");
		return;
	}

	private static void managementConsole(String Username, String Password) {

		CurrentUser = Handler.Login(Username, Password, true);
		if (CurrentUser == null) {
			System.out.println("Employee account not found.");
			return;
		}
		if (CurrentUser.getStatus() != 2) {
			System.out.println("You don't work here. Get a job.");
			return;
		}

		System.out.println("Login verified. Hello, " + CurrentUser.getUsername() + "!");
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
		TargetUser = Handler.viewUser(value);
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
					TargetUser.setStatus(TargetUser.getStatus() + 1);
					System.out.println("User status promoted.");
					Handler.updateUser(TargetUser, null);
					return;
				} else if (value.equals("N") || value.equals("N")) {
					return;
				} else {
					
				}
			}
			
			
			
			
		}

	}

	private static void LogViewer() {
		ArrayList<Transfer> Logs = Handler.getTransfers(null);

		System.out.println("Displaying all transactions");
		System.out.print(Logs);
	}

	private static void registerConsole(String Username, String Password) {
		CurrentUser = new User(0, Username, 0);
		Handler.updateUser(CurrentUser, Password);
		System.out.println("New User " + Username + " created!");
		System.out.println("Please wait for approval.");
		return;
	}

}
