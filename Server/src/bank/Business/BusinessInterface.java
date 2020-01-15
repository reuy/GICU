package bank.Business;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.log4j.Logger;

import bank.Console;
import bank.DBInterface;
import bank.Transfer;
import bank.User;

public interface BusinessInterface {

	/*
	 * Asks the DB Interface for users, then modifies them according to what the web
	 * interface requests, and sends the modified users back to the DB Interface.
	 * All balances are modified by
	 */

	/*
	 * Files a new transfer with the Database. The Database uses transfers to manage
	 * all money on its own terms.
	 */
	abstract void reportTransfer(Transfer Transfer);



};
