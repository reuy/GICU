package bank;

import java.sql.Date;

public class Transfer {
	@Override
	public String toString() {
		return "Transfer #" + id + ": "
	+ SenderName + " sends " + amount + " to " + RecipientName + " on " + date + ". STATUS CODE: " + Status;
	}
	byte Status;
	/*
	 * 0: Archived(Ignore)
	 * 1: Pending Approval
	 * 2: Approved
	 * */
	
	String SenderName;
	String RecipientName;
	int amount;
	Date date;
	int id;
	
	
	
	public Transfer() {
		super();
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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
