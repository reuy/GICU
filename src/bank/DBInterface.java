package bank;

import java.util.ArrayList;

/* When the user logs in, Console will call Login(Username, Hash) to create a User using info from the database.
 * 
 * When the user makes transactions, console will call Update(User) to update the corresponding user in the database.*/

//Set of functions expected by whatever program will implement the database.
public interface DBInterface {
	
	
	/*
	 * Returns a User Object associated with a given username and password, complete with a list of all transfers.
	 * First, Hash the given password. Look up the given username, match it with the
	 * password hash. If no matching hash, return Null.
	 * 
	 * Otherwise, gather all the info for the user, including the pending transfers,
	 * shove them into an array, and pack it as a user object.
	 */
	public abstract User Login(String Username, String password, boolean EmployeeCheck);
	
	/*
	 * Returns a User Object associated with a given username.
	 * Look up the given username. If no matching username, return Null.
	 * 
	 * Otherwise, gather all the info for the user, including the pending transfers,
	 * shove them into an array, and pack it as a user object.
	 * 
	 * In a normal situation, this would require the user to pass in an Employee as a security measure.
	 */
	public abstract User viewUser(String Username);

	/*
	 * Updates the matching user in the Database to match the given user
	 * Password will also be hashed and set.
	 * Don't forget to update the transfers, as that's a separate database.
	 * Used primarily to create new accounts
	 */
	public abstract void updateDBUser(User user, String Password);
	
	/*
	 * Updates the given user to match the matching user in the database.
	 * Don't forget to get the transfers, as that's a separate database.
	 * Used primarily to create new accounts
	 */
	public abstract void updateLocalUser(User user);

	/*
	 * Use userID as an SQL lookup key for all pending transfers, as both sender and
	 * recipient. DOES NOT RETURN ARCHIVED TRANSFERS
	 * 
	 * Requires an external userID, as employees need to be able to access any user.
	 * If it's above zero, create an arrayList and populate it.
	 */
	public abstract ArrayList<Transfer> getTransfers(String Username);
	
	/* 
	 * Returns all transfers
	 * If it's above zero, create an arrayList and populate it.
	 */
	public abstract ArrayList<Transfer> getTransfers();
	
	/*
	 * Uploads the transfer manifest back to the database, updating all matching ids and inserting new ones.
	 */
	public abstract void updateTransfers(ArrayList<Transfer> transferManifest);

}
