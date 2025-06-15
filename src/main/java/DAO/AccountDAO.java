package DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Model.Account;
import Util.ConnectionUtil;

public class AccountDAO {
	// Create an instance of the ConnectionUtil() object (for database connectivity)
	ConnectionUtil connection = new ConnectionUtil();
	
	// A Constructor-method appears to be unnecessary (see WK04 : D04 : Data Access Object)
	
	public boolean createAccount(Account newAccount) throws SQLException {
		// Open a database connection (using ConnectionUtil() singleton)
		ConnectionUtil.getConnection();
		
		// The following steps check if account exists prior to insertion
        // Query the database for the provided username
		String unsafeSql = "SELECT username FROM account WHERE username = ?";
		PreparedStatement safeSql = ConnectionUtil.getConnection().prepareStatement(unsafeSql);
		safeSql.setString(1, newAccount.getUsername());
		ResultSet rs = safeSql.executeQuery();
		int numOfEntries = 0;
		while(rs.next()) {
			numOfEntries = numOfEntries + 1;
		}
		
		// Check if account-username already exists (where > 0 suggests it exists)
		if (numOfEntries != 0) {
			// Close a database connection (using ConnectionUtil())
			ConnectionUtil.getConnection().close();
			
			// Terminate insertion
			return false;
		} else {
			// Username does not appear in database... proceed to insertion
			unsafeSql = "INSERT INTO account (username, password) VALUES (?, ?)";
			safeSql = ConnectionUtil.getConnection().prepareStatement(unsafeSql);
			safeSql.setString(1, newAccount.getUsername());
			safeSql.setString(2, newAccount.getPassword());
			numOfEntries = safeSql.executeUpdate();
			
			// Retrieve the results of the statement (to confirm insertion success)
			if (numOfEntries == 1) {
				// Update the Account object (to set the ID)
				unsafeSql = "SELECT account_id FROM account WHERE username = ?";
				safeSql = ConnectionUtil.getConnection().prepareStatement(unsafeSql);
				safeSql.setString(1, newAccount.getUsername());
				rs = safeSql.executeQuery();
				while(rs.next()) {
					newAccount.setAccount_id(rs.getInt("account_id"));
				}
				
				// Close a database connection (using ConnectionUtil())
				ConnectionUtil.getConnection().close();
				
				// Successful insertion
				return true;
			} else {
				// Close a database connection (using ConnectionUtil())
				ConnectionUtil.getConnection().close();
				
				// Failed insertion
				return false;
			}
		}
	}
	
	public boolean checkLoginCredentials(Account account) throws SQLException{
		// Open a database connection (using ConnectionUtil())
		ConnectionUtil.getConnection();
		
        // The following steps check for the existence of the provided username (and verify the password is correct)
		String unsafeSql = "SELECT * FROM account WHERE username = ?";
		PreparedStatement safeSql = ConnectionUtil.getConnection().prepareStatement(unsafeSql);
		safeSql.setString(1, account.getUsername());
		ResultSet rs = safeSql.executeQuery();
		
		// Loop through number of entries
		int numOfEntries = 0;
        int accountID = 0;
		String resultPassword = "";
		while(rs.next()) {
            // Extract account_id and password from result-set
            accountID = rs.getInt("account_id");
			resultPassword = rs.getString("password");
			numOfEntries = numOfEntries + 1;
		}
		
		// Branch based on number of returned results
		if (numOfEntries == 0) {
			// If number of results is zero, then username does not exist
			ConnectionUtil.getConnection().close();
			return false;
		} else {
			// Else... username DOES exist... proceed to cross-ref password
			if (account.getPassword().equals(resultPassword)) {
				// Database password equals provided password
				// Set account instance ID to actual ID
				account.setAccount_id(accountID);

				ConnectionUtil.getConnection().close();
				return true;
			} else {
				ConnectionUtil.getConnection().close();
				return false;
			}
		}
	}
}
