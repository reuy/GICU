package bank.DBAccess;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import bank.*;
import bank.util.BankUtil;

import java.sql.*;

public class DBHandler implements DBInterface {
	private static final Logger log = Logger.getLogger(DBHandler.class);
	static User CurrentUser;
	static User TargetUser;
	Connection DBconn;
	ResultSet DBrslt;
	PreparedStatement DBstmt;
	
	@Override
	public void Login(String Username, String password, boolean Employee) throws DBAccessException{
	try {
		initializeConnection();
		// Look up the given username and password.
		String sql = "SELECT * FROM UserList WHERE Username = ? AND PasswordHash = ? ";
		DBstmt = DBconn.prepareStatement(sql);
		DBstmt.setString(1, Username);
		DBstmt.setInt(2, password.hashCode() % 99999999);

		DBrslt = DBstmt.executeQuery();

		if (!DBrslt.next())
			throw new DBAccessException(1, "User not found");

		User user = new User(DBrslt.getInt("Status"), DBrslt.getString("Username"), DBrslt.getInt("Balance"));
		user.setPendingTransfers(getTransfers(user.username));
		
		if(user.getStatus() < 2 && Employee == true)
			throw new NoPermissionException(2, "User not an Employee");
		
		if(user.getStatus() == 0)
			throw new NoPermissionException(1, "User not Registered");

		
		CurrentUser = user;
	} catch (NoPermissionException e) {
		throw e;
	} catch (SQLException e) {
		log.warn(e);
		e.printStackTrace();
	} finally {
		closeConnection();
	}

}

	public User getUser(){
		return getUser(CurrentUser.username);
	}
	@Override
	public User getUser(String Username) throws NoPermissionException {
		//Only employees can fetch a user that is not the current user
		if(Username != CurrentUser.getUsername() && CurrentUser.getStatus() != 2)
			throw new NoPermissionException(2, "User not an Employee");
		

		try {
			initializeConnection();
			// Look up the given username and password.
			String sql = "SELECT * FROM UserList WHERE Username = ?";
			DBstmt = DBconn.prepareStatement(sql);
			DBstmt.setString(1, Username);

			DBrslt = DBstmt.executeQuery();

			if (!DBrslt.next())
				return null;

		User user = new User(DBrslt.getInt("Status"), DBrslt.getString("Username"), DBrslt.getInt("Balance"));
		user.setPendingTransfers(getTransfers(user.getUsername()));	
		return user;
		} catch (RuntimeException e) {
			throw e;
		} catch (SQLException e) {
			log.warn(e);
			e.printStackTrace();
			return null;
		} finally {
			closeConnection();
		}
	}
	@Override
	public void promoteUser(String Username) throws NoPermissionException, DBAccessException {		if(CurrentUser.getStatus() != 2)
		throw new NoPermissionException(2, "User not an Employee");
	
	User current = getUser(Username);
	
	if (current == null) {
		throw new DBAccessException(1,"User not found");
	}
	
	if(current.getStatus()<2) {
	current.setStatus(current.getStatus()+1);
	updateDBUser(current, null);
	}
	
	}
	@Override
	public void RegisterUser(String Username, String Password) throws DBAccessException {
		if(checkUser(Username)) {
			throw new DBAccessException(3,"User already exists");
		}
		User newUser = new User(0, Username, 0);
		updateDBUser(newUser, Password);
	}
	@Override
	public ArrayList<bank.Transfer> getLogs(String Username) throws NoPermissionException {
		if(CurrentUser.getStatus() != 2)
		throw new NoPermissionException(2, "User not an Employee");
	
	return getTransfers(null);
	}
	@Override
	public ArrayList<bank.Transfer> getPendingTransactions() {

		return BankUtil.filterTransfers(getTransfers(CurrentUser.getUsername()), CurrentUser.getUsername(), (byte) 2);
	}
	@Override
	public ArrayList<bank.Transfer> acceptPendingTransaction(int TransactionID) throws DBAccessException {
		for (Transfer iteration : CurrentUser.getPendingTransfers()) {
			if(iteration.getId() == TransactionID) {
				iteration.setStatus(2);
			}
		}
		
		Synchronize();
		return getPendingTransactions();
	}
	@Override
	public void Deposit(int amount) throws IllegalArgumentException {
		if(amount < 0 && -amount > CurrentUser.getBalance()) {
		throw new IllegalArgumentException("Invalid Transaction");
		}
		Transfer NewTransfer = new Transfer(CurrentUser.getUsername(), amount);
		NewTransfer.setStatus(3);
		CurrentUser.getPendingTransfers().add(NewTransfer);
		Synchronize();
		}
	@Override
	public void Transfer(int amount, String targetname) throws DBAccessException {
		if(!checkUser(targetname))
			throw new DBAccessException(1, "Target user not found");
		
		Transfer newTrans = new Transfer(CurrentUser.getUsername(), targetname, amount, -1);
		
		CurrentUser.getPendingTransfers().add(newTrans);
		Synchronize();
	}
	
	/*
	 * PRIVATE METHODS
	 * */
	
	private void initializeConnection() throws RuntimeException {
		try {
			// Establish connection to the Database
			Class.forName("oracle.jdbc.OracleDriver");
			DBconn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "SYSTEM", "1111");

		} catch (ClassNotFoundException ef) {
			throw new RuntimeException("ERROR: This application does not seem to have been configured properly.");
		} catch (SQLException es) {
			throw new RuntimeException("ERROR: Cannot connect to bank server");
		}

	}

	/*
	 * Shuts down the connection. Called in the finally block of all Database
	 * actions
	 */
	private void closeConnection() {
		try {
			if (DBconn != null)
				DBconn.close();
			if (DBrslt != null)
				DBrslt.close();
			if (DBstmt != null)
				DBstmt.close();
		} catch (Exception e) {
		}

	}

	
	private ArrayList<Transfer> getTransfers(String Username) {
		try {
			initializeConnection();

			/* Get transfers for both incoming and outgoing transfers. */
			ArrayList<Transfer> List = new ArrayList<Transfer>();

			/* Populate the given list with incoming transfers involving the user */
			String sql = "SELECT * FROM TransferList";
			
			if(Username != null) {
			sql = "SELECT * FROM TransferList WHERE RecipientName= ? OR SenderName= ?";
			DBstmt = DBconn.prepareStatement(sql);
			DBstmt.setString(1, Username);
			DBstmt.setString(2, Username);
		}
			DBrslt = DBstmt.executeQuery();

			// Run through the list
			while (DBrslt.next()) {
				Transfer New = new Transfer();
				New.setId(DBrslt.getInt("TRANSFERID"));
				New.setAmount(DBrslt.getInt("SENDAMOUNT"));
				New.setTime(DBrslt.getTime("TRANSFERTIME"));
				New.setRecipient(DBrslt.getString("RecipientNAME"));
				New.setSender(DBrslt.getString("SenderNAME"));
				New.setStatus(DBrslt.getInt("TRANSFERSTATUS"));
				log.debug("Retrieved transfer from database:\n	" + New);
				List.add(New);
			}

			return List;
		} catch (RuntimeException e) {
			log.warn("WARNING: Caught " + e.toString() + ".\n" + e.getStackTrace());
			return null;
		} catch (SQLException e) {
			log.warn("WARNING: Caught " + e.toString() + ".\n" + e.getStackTrace());
			return null;
		} finally {
			closeConnection();
		}
	}


	private void updateTransfers(ArrayList<Transfer> List) {
		try {
			initializeConnection();

			// For each element of the given list...
			for (Transfer current : List) {
				
				// Check if the ID of the transfer exists
				String sql = "SELECT * FROM TransferList WHERE TransferID = ? ";/*
				DBstmt = DBconn.prepareStatement(sql);
				DBstmt.setInt(1, current.getId());
				DBstmt = DBconn.prepareStatement(sql);
				DBrslt = DBstmt.executeQuery();*/

				//If so, prepare an update statement
				if (current.getId()!= -1) {
					log.info("Regular Update of transfer with id " + current.getId());

					sql = "UPDATE TransferList "
							+ "SET TransferStatus = ?, "
							+ "RECIPIENTName = ?, "
							+ "SENDERName = ?, "
							+ "SendAMOUNT = ?, "
							+ "TransferTIME = ? "
							+ "WHERE TransferID = ? ";
					DBstmt = DBconn.prepareStatement(sql);

					DBstmt.setInt(1, current.getStatus());
					DBstmt.setString(2, current.getRecipient());
					DBstmt.setString(3, current.getSender());
					DBstmt.setInt(4, current.getAmount());
					DBstmt.setTime(5, current.getTime());
					DBstmt.setInt(6, current.getId());
				} else {
					//Otherwise, create a new item
					
					log.info("Creation of new Transfer.");
					sql = "insert into TransferList(TransferStatus, RecipientName, SenderName, SendAmount, TransferTime) VALUES (?, ?, ?, ?, ?)";
					DBstmt = DBconn.prepareStatement(sql);
					DBstmt.setInt(1, current.getStatus());
					DBstmt.setString(2, current.getRecipient());
					DBstmt.setString(3, current.getSender());
					DBstmt.setInt(4, current.getAmount());
					DBstmt.setTime(5, current.getTime());
				}
				
				//Activate query
				DBstmt.execute();
			}

			return;
		} catch (RuntimeException e) {
			log.warn("WARNING: Caught " + e.toString() + ".\n" + e.getStackTrace());
			return;
		} catch (SQLException e) {
			log.warn("WARNING: Caught " + e.toString() + ".\n" + e.getStackTrace());
			return;
		} finally {
			closeConnection();
		}

	}
	
	private boolean checkUser(String Username) {
		try {
			initializeConnection();
			// Look up the given username and password.
			String sql = "SELECT * FROM UserList WHERE Username = ?";
			DBstmt = DBconn.prepareStatement(sql);
			DBstmt.setString(1, Username);

			DBrslt = DBstmt.executeQuery();

			if (!DBrslt.next())
				return false;

			return true;
		} catch (RuntimeException e) {
			throw e;
		} catch (SQLException e) {
			log.warn(e);
			e.printStackTrace();
			return false;
		} finally {
			closeConnection();
		}
	}
	
	
	public void updateDBUser(User user, String password) {

		try {
			initializeConnection();

			// First, search for the given user
			String sql = "SELECT * FROM UserList WHERE Username = ?";
			DBstmt = DBconn.prepareStatement(sql);
			DBstmt.setString(1, user.username);
			DBrslt = DBstmt.executeQuery();

			// 1) No user exists - CREATE new user
			if (!DBrslt.next() && password != null) {
				log.debug("Creation of user " + user.getUsername() + " with new password.");
				sql = "insert into UserList(Username, PasswordHash, Balance, Status) VALUES (?, ?, ?, ?)";
				DBstmt = DBconn.prepareStatement(sql);
				DBstmt.setString(1, user.getUsername());
				DBstmt.setInt(2, password.hashCode() % 99999999);
				DBstmt.setInt(3, user.getBalance());
				DBstmt.setInt(4, 0);

			} else if (password != null) {
				// 2) User exists with a given password, UPDATE user
				log.debug("Special Update of user " + user.getUsername() + "with new password.");

				sql = "Update UserList SET PasswordHash = ?, Balance = ?, Status = ? WHERE Username = ?";
				DBstmt = DBconn.prepareStatement(sql);

				DBstmt.setInt(1, password.hashCode() % 99999999);
				DBstmt.setInt(2, user.getBalance());
				DBstmt.setInt(3, user.getStatus());
				DBstmt.setString(4, user.getUsername());
			} else {
				// 2) User exists with no given password, UPDATE user
				log.debug("Regular Update of user " + user.getUsername() + ". No password given.");

				sql = "Update UserList SET Balance = ?, Status = ? WHERE Username = ? ";
				DBstmt = DBconn.prepareStatement(sql);

				DBstmt.setInt(1, user.getBalance());
				DBstmt.setInt(2, user.getStatus());
				DBstmt.setString(3, user.getUsername());
			}
			DBstmt.execute();
			
			updateTransfers(user.getPendingTransfers());
			

		} catch (SQLException e) {
			log.warn(e);
		} finally {
			closeConnection();
		}
	}

	public void updateLocalUser(User user) {
		User update = getUser(user.username);
		
		user.setBalance(update.getBalance());
		user.setPendingTransfers(update.getPendingTransfers());
		user.setStatus(update.getStatus());
	}
	
	private void Synchronize() {
		updateDBUser(CurrentUser, null);
		updateLocalUser(CurrentUser);
	}
}
