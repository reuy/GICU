/**
 * 
 */
package bankTest;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;
import bank.*;
import org.junit.jupiter.api.Test;
import junit.framework.AssertionFailedError;

class ConnectionTester {
	DBInterface Handler = new DBHandler();

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
		User testUser = Handler.Login("Testy-McTesterson", "#NotMyPassword", false);
		assertNull(testUser,"Test user somehow managed to get in with wrong password!");
	}
	
	@Test
	void testDBLogin() {
		User testUser = Handler.Login("Testy-McTesterson", "Password", false);
		boolean nameCheck = testUser.getUsername().equals("Testy-McTesterson");
		assertTrue("Username returned by DB did not match - got " + testUser.getUsername(), nameCheck);
	}
}
