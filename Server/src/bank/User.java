/**
 * 
 */
package bank;

import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * @author saito
 *
 */
public class User {
	private static final Logger log = Logger.getLogger(User.class);
	
	public String username;
	private int balance;
	ArrayList<Transfer> pendingTransfers;
	private int status; //0 = Pending Approval, 1 = Approved Customer, 2 = Employee, 9 = marked for deletion
	
	
	
	//the hash must already be inputted.
	public User(int status, String username, int balance) {
		super();
		this.status = status;
		this.username = username;
		this.balance = balance;
		pendingTransfers = new ArrayList<Transfer>();
	}
	

	

	
	
	@Override
	public String toString() {
		String title = "???";
		switch (status) {
		case 0 :
			title = "Pending Approval";
			break;
			
		case 1 :
			title = "Customer";
			break;
			
		case 2:
			title = "Employee";
			break;
			}
		
		if(title.equals("???")) {
			log.warn("WARNING: Unknown user type!");
			throw new RuntimeException("WARNING: Unknown user type!");
		}
		return "[User: " + username + ", balance= " + balance + ", status= " + title + "]";
	}






	//GetSets
	public int getStatus() {
		return status;
	}
	public void setStatus(int i) {
		this.status = i;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}






	
	public ArrayList<Transfer> getPendingTransfers() {
		return pendingTransfers;
	}






	public void setPendingTransfers(ArrayList<Transfer> pendingTransfers) {
		this.pendingTransfers = pendingTransfers;
	}


}
