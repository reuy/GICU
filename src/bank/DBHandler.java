package bank;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import java.sql.*;

public class DBHandler implements DBInterface {
	private static final Logger log = Logger.getLogger(DBHandler.class);
	Connection DBconn;
	ResultSet DBrslt;
	PreparedStatement DBstmt;

	/*
	 * Initializes the database connection. Called in the try block of all Database
	 * actions
	 */
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

	/*
	 * Returns a User Object associated with a given username and password, complete
	 * with a list of all transfers. First, Hash the given password. Look up the
	 * given username, match it with the password hash. If no matching hash, return
	 * null. If EmployeeCheck is true, and the user's status is != 2, return null.
	 * 
	 * Otherwise, gather all the info for the user, including the pending transfers,
	 * shove them into an array, and pack it as a user object.
	 */
	@Override
	public User Login(String Username, String password, boolean EmployeeCheck) {
		try {
			initializeConnection();
			// Look up the given username and password.
			String sql = "SELECT * FROM UserList WHERE Username = ? AND PasswordHash = ? ";
			DBstmt = DBconn.prepareStatement(sql);
			DBstmt.setString(1, Username);
			DBstmt.setInt(2, password.hashCode() % 99999999);

			DBrslt = DBstmt.executeQuery();
			
			if(!DBrslt.next())
			return null;
			
			User user = new User(DBrslt.getInt("Status"), DBrslt.getString("Username"), DBrslt.getInt("Balance")); 
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
	public User setPassword(String Username, String Password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateUser(User user, String password) {

		try {
			initializeConnection();

			// First, search for the given user
			String sql = "SELECT * FROM UserList WHERE Username = ?";
			DBstmt = DBconn.prepareStatement(sql);
			DBstmt.setString(1, user.username);
			DBrslt = DBstmt.executeQuery();

			// 1) No user exists - CREATE new user
			if (!DBrslt.next() && password != null) {
				log.info("Creation of user " + user.getUsername() + " with new password.");
				sql = "insert into UserList(Username, PasswordHash, Balance, Status) VALUES (?, ?, ?, ?)";
				DBstmt = DBconn.prepareStatement(sql);
				DBstmt.setString(1, user.getUsername());
				DBstmt.setInt(2, password.hashCode() % 99999999);
				DBstmt.setInt(3, user.getBalance());
				DBstmt.setInt(4, 0);

			} else if (password != null) {
				// 2) User exists with a given password, UPDATE user
				log.info("Special Update of user " + user.getUsername() + "with new password.");

				sql = "Update UserList SET PasswordHash = ?, Balance = ?, Status = ? WHERE Username = ?";
				DBstmt = DBconn.prepareStatement(sql);

				DBstmt.setInt(1, password.hashCode() % 99999999);
				DBstmt.setInt(2, user.getBalance());
				DBstmt.setInt(3, user.getStatus());
				DBstmt.setString(4, user.getUsername());
			} else {
				// 2) User exists with no given password, UPDATE user
				log.info("Regular Update of user " + user.getUsername() + ". No password given.");

				sql = "Update UserList SET Balance = ?, Status = ? WHERE Username = ? ";
				DBstmt = DBconn.prepareStatement(sql);

				DBstmt.setInt(1, user.getBalance());
				DBstmt.setInt(2, user.getStatus());
				DBstmt.setString(3, user.getUsername());
			}
			DBstmt.execute();

		} catch (SQLException e) {
			log.warn(e);
		} finally {
			closeConnection();
		}
	}

	@Override
	public ArrayList<Transfer> getTransfers(String Username) {
		try {

			/* Get transfers for both incoming and outgoing transfers. */
			ArrayList<Transfer> List = new ArrayList<Transfer>();

			/* Populate the given list with incoming transfers involving the user */
			String sql = "SELECT * FROM TransferList WHERE STATUS < 0 AND (Recipient= ? OR Sender= ? )";
			DBstmt = DBconn.prepareStatement(sql);
			DBstmt.setString(1, Username);
			DBstmt.setString(2, Username);
			DBstmt = DBconn.prepareStatement(sql);
			DBrslt = DBstmt.executeQuery();

			// Run through the list
			while (DBrslt.next()) {
				Transfer New = new Transfer();
				New.setId(DBrslt.getInt("TRANSFERID"));
				New.setAmount(DBrslt.getInt("AMOUNT"));
				New.setDate(DBrslt.getDate("TIME"));
				New.setRecipient(DBrslt.getString("Recipient"));
				New.setSender(DBrslt.getString("Sender"));
				log.debug("Inserted new transfer into database:\n	" + New);
				List.add(New);
			}

			return null;
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

	@Override
	public User viewUser(String Username) {
		try {
			initializeConnection();
			// Look up the given username and password.
			String sql = "SELECT * FROM UserList WHERE Username = ?";
			DBstmt = DBconn.prepareStatement(sql);
			DBstmt.setString(1, Username);

			DBrslt = DBstmt.executeQuery();
			
			if(!DBrslt.next())
			return null;
			
			User user = new User(DBrslt.getInt("Balance"), DBrslt.getString("Username"), DBrslt.getInt("Status")); 
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
	public void updateTransfers(ArrayList<Transfer> transferManifest) {
		// TODO Auto-generated method stub

	}

}
