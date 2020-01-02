package bank;

import java.sql.Time;

public class Transfer {
	@Override
	public String toString() {
		return "Transfer #" + id + ": "
	+ SenderName + " sends " + amount + " to " + RecipientName + " on " + Timestamp + ". STATUS CODE: " + Status;
	}
	byte Status = 1;
	/*
	 * 0: Archived(Ignore)
	 * 1: Pending Approval
	 * 2: Approved
	 * */
	
	String SenderName;
	String RecipientName;
	int amount;
	Time Timestamp;
	int id = -1;
	
	
	
	public Transfer() {
		super();
	}
	public Transfer(String SenderName, String RecipientName, int Amount) {
		super();
		
		java.util.Date today = new java.util.Date();
		this.SenderName = SenderName;
		this.RecipientName = RecipientName;
		this.amount= Amount;
		this.Timestamp = new Time(today.getTime());
		
	}
	public byte getStatus() {
		return Status;
	}
	public void setStatus(byte status) {
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
}
