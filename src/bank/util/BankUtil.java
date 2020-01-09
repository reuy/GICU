package bank.util;

import java.util.ArrayList;
import java.util.Iterator;

import bank.Transfer;

public class BankUtil {
	/*
	 * Goes into a list of transfers and filters out...
	 * Transfers not marked as 1
	 * Transfers where the given Username is not the Sender(Mode 1 - SENDER ONLY)
	 * Transfers where the given Username is not the Recipient(Mode 2 - RECIPIENT
	 * ONLY) 
	 * Transfers where the given username is not either(mode 0)
	 */
	public static ArrayList<Transfer> filterTransfers(ArrayList<Transfer> List, String Username, byte mode) {
		if (List.isEmpty()) {
			return List;
		}

		Iterator<Transfer> iter = List.iterator();

		while (iter.hasNext()) {
			Transfer current = iter.next();
			if (current.getStatus() != 1) {
				iter.remove();
				continue;
			}
			if (current.getSender() != Username && current.getRecipient() != Username) {
				iter.remove();
				continue;
			}

			if (mode == 1 && current.getSender() != Username) {
				iter.remove();
				continue;
			}

			if (mode == 2 && current.getRecipient() != Username) {
				iter.remove();
				continue;
			}
		}
		return List;

	}
}
