package bank;

import java.sql.Time;

public class Transfer {
	int Status = 1;

	/*
	 * 0: Archived(Ignore) 
	 * 1: Pending Approval
	 * 2: Approved - DB will transfer funds and set to zero
	 * 3: Deposit/Withdrawal - DB will add/subtract funds and set to zero
	 */

	String SenderName;
	String RecipientName;
	int amount;
	Time Timestamp;
	int id = -1;

	@Override
	public String toString() {
		if (SenderName != RecipientName) {
			return "Transaction #" + id + ": " + SenderName + " sends " + amount + " to " + RecipientName + " on "
					+ Timestamp + ". STATUS CODE: " + Status;
		} else if (amount < 0) {
			return "Transaction #" + id + ": " + SenderName + " deposited " + amount + " on " + Timestamp
					+ ". STATUS CODE: " + Status;
		} else {
			return "Transaction #" + id + ": " + SenderName + " withdrew " + amount + " on " + Timestamp
					+ ". STATUS CODE: " + Status;
		}
	}

	public Transfer() {
		super();
		java.util.Date today = new java.util.Date();
		this.Timestamp = new Time(today.getTime());
	}

	/*
	 * Transfer Constructor - Sets Status to 1
	 * */
	public Transfer(String SenderName, String RecipientName, int Amount, int id) {
		super();

		java.util.Date today = new java.util.Date();
		this.SenderName = SenderName;
		this.RecipientName = RecipientName;
		this.amount = Amount;
		this.Timestamp = new Time(today.getTime());
		this.id = id;
		this.Status = 1;
	}
	
	//Depositor Constructor - Sets Status to 3
	public Transfer(String DepositorName, int Amount) {
		super();

		java.util.Date today = new java.util.Date();
		this.SenderName = DepositorName;
		this.RecipientName = DepositorName;
		this.amount = Amount;
		this.Timestamp = new Time(today.getTime());
		this.id = -1;
		this.Status = 3;
	}
	
	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public String getSender() {
		return SenderName;
	}

	public void setSender(String sender) {
		SenderName = sender;
	}

	public String getRecipient() {
		return RecipientName;
	}

	public void setRecipient(String string) {
		RecipientName = string;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Time getTime() {
		return Timestamp;
	}

	public void setTime(Time Timestamp) {
		this.Timestamp = Timestamp;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String transType() {
		if(SenderName != RecipientName)
			return "Transfer";
		if(amount < 0)
			return "Withdrawal";
		if(amount >= 0)
			return "Deposit";
		return "Unknown";
	}
}
