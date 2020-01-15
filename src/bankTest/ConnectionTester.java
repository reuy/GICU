/**
 * 
 */
package bankTest;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;

import javax.naming.NoPermissionException;

import bank.*;
import bank.DBAccess.*;

import org.junit.jupiter.api.Test;
import junit.framework.AssertionFailedError;

class ConnectionTester {
	DBInterface Handler = new DBHandler();
	int TestDeposit = 1337;
	int TestTransactionID;

	@Test
	void testDBConnection() {
		try {
			// Establish connection to the Database
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection TestConnection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "SYSTEM", "1111");
			
			Statement stmt = TestConnection.createStatement();
			
			try {
			assertTrue(TestConnection.isValid(10));}
			catch(AssertionFailedError e){
				fail("Connection not valid - " + e );
			}
			
			
			try {
				
				String CreationString = "CREATE TABLE Test_Table (TestColumn Number, TestColumn2 Number)";
				
			stmt.executeUpdate(CreationString); }
			catch (SQLException ec) {
				fail("Could not create test table - \n" + ec);
			}

			
			
			
			
			
			try {
				String DestructionString = "drop table test_Table";
				stmt.executeUpdate(DestructionString);
			}
			catch(SQLException ec) {
				TestConnection.close();
				stmt.close();
				fail("Could not drop test table  - \n" + ec);
			} finally {
				
			}
			
			TestConnection.close();


		} catch (ClassNotFoundException ef) {
			fail("Class not found");
		} catch (SQLException es) {
			fail("Class found, but connection failed - " + es);
		} finally {
		}
	}

	@Test
	void testDBFailedLogin() {
		try {
		Handler.Login("Testy-McTesterson", "#NotMyPassword", false);
		fail("Login did not return errorcode 1 when given wrong password");
		} catch (DBAccessException e) {
			assertTrue(e.code()==1, "Test user succeded during login!");
		}
		
	}
	
	@Test
	void testDBFailedEmployeeLogin() {
		try {
		Handler.Login("Testy-McTestedson", "Password", true);
		fail("Test user was not turned down during login!");
		} catch (RuntimeException e) {}
		
	}
	
	
	@Test
	void testDBLogin() {
		Handler.Login("Testy-McTesterson", "Password", false);
		boolean nameCheck = Handler.getUser().getUsername().equals("Testy-McTesterson");
		assertTrue("Username returned by DB did not match - got " + Handler.getUser().getUsername(), nameCheck);
	}
	
	@Test
	void testDeposit() {
		Handler.Login("Testy-McTesterson", "Password", false);
		int originalval = Handler.getUser().getBalance();
		Handler.Deposit(TestDeposit);
		int newval = Handler.getUser().getBalance();
		assertEquals(newval, originalval + TestDeposit);
	}
	
	@Test
	void testWithdrawal() {
		Handler.Login("Testy-McTesterson", "Password", false);
		int originalval = Handler.getUser().getBalance();
		Handler.Deposit(-TestDeposit);
		int newval = Handler.getUser().getBalance();
		assertEquals(newval, originalval - TestDeposit);
	}
	
	@Test
	void testTransfer() {
		Handler.Login("Testy-McTestedson", "Password", false);
		Handler.Deposit(TestDeposit);
		Handler.Transfer(TestDeposit, "Testy-McTesterson");
		
	//------------/	
		
		int RecieveOriginal= 0;
		int SendOriginal = 0;
		Handler.Login("Testy-McTesterson", "Password", true);
		try {
			
			//Original value of McTestERson, who WILL GET the cash
		RecieveOriginal = Handler.getUser().getBalance();
		
			//Original calue of McTestEDson, who HAD the cash
		SendOriginal = Handler.getUser("Testy-McTestedson").getBalance();
		
		} catch(RuntimeException e) {
			fail("Couldn't retrieve original balance values!");
		}
		if(Handler.getPendingTransactions() == null)
			fail("Table value was null!");
		if(Handler.getPendingTransactions().size()<1)
			fail("Table was empty!");
		
		Transfer TargetTransaction = null;
		
		for (Transfer iter : Handler.getPendingTransactions()) {
			if(iter.getStatus() == 1 
					&& iter.getRecipient().equals("Testy-McTesterson")
					&& iter.getSender().equals("Testy-McTestedson")
					&& iter.getAmount() == TestDeposit) {
				TargetTransaction=iter;
				break;}
		}
		
		if(TargetTransaction == null)
			fail("Couldn't find the target transaction!");
		
		Handler.acceptPendingTransaction(TargetTransaction.getId());
		
		assertEquals(SendOriginal-TestDeposit , Handler.getUser("Testy-McTestedson").getBalance());
		
		assertEquals(RecieveOriginal+TestDeposit, Handler.getUser("Testy-McTesterson").getBalance());
	}

	
}
