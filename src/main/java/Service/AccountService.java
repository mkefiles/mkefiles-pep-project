package Service;

import java.sql.SQLException;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
	
	private AccountDAO accountDao;
	
	public AccountService() {
		// Create an instance of the Data Access Object to work with database
		accountDao = new AccountDAO();
	}
	
	public boolean verifyAndCreateAccount(Account account) throws SQLException {
		// Confirm that username is NOT blank
		if (account.getUsername().length() == 0) {
			return false;
		} else {
			if (account.getPassword().length() < 4) {
				return false;
			} else {
				// Pass to AccountDAO
				return accountDao.createAccount(account);
			}
		}
	}
	
	public boolean loginAccount(Account account) throws SQLException {
		// Pass data straight through to DAO (i.e., no verification/validation necessary)
		return accountDao.checkLoginCredentials(account);
	}

}