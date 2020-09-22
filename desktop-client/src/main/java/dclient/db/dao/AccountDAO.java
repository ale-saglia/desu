package dclient.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import dclient.model.*;

public class AccountDAO {
	/**
	 * This function is meant to add a single object (Account) to the DB and does
	 * nothing if exist another account with the same primary key.
	 * 
	 * @param conn    the Connection object to the DB
	 * @param account the account you want to add to the DB
	 * 
	 * @return the number of database row that has been changed by the function
	 */
	public static int newAccount(Connection conn, Account account) {
		String query = "insert into accounts.accounts (fiscalcode,\"name\",numbervat,atecocode,legal_address,customer_category,descriptor) values ( ? , ? , ? , ? , ? , ? , ? ) ";
		int rowsAffected = 0;

		try {
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, account.getFiscalCode());
			st.setString(2, account.getName());
			st.setString(3, account.getNumberVAT());
			st.setString(4, account.getAtecoCode());
			st.setString(5, account.getLegalAddress());
			st.setString(6, account.getCategory());
			st.setString(7, account.getDescriptor());

			rowsAffected = st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database o campo già esistente");
			return -1;
		}

		System.out.println("Rows updated ACCOUNT => " + rowsAffected);
		return 0;
	}

	/**
	 * This function is meant to edit a single object (Account) on the DB.
	 * 
	 * @param conn          the Connection object to the DB
	 * @param oldFiscalCode the fiscal code of the Account prior to editing
	 * @param account       the new account fields you want to be added to the DB
	 * 
	 * @return the number of database row that has been changed by the function
	 */
	public static int updateAccount(Connection conn, String oldFiscalCode, Account account) {
		String query = "UPDATE accounts.accounts SET fiscalcode = ?, \"name\" = ?, numbervat = ?, atecocode = ?, legal_address = ?, customer_category = ? , descriptor = ? WHERE fiscalcode = ? ";
		int rowsAffected = 0;

		try {
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, account.getFiscalCode());
			st.setString(2, account.getName());
			st.setString(3, account.getNumberVAT());
			st.setString(4, account.getAtecoCode());
			st.setString(5, account.getLegalAddress());
			st.setString(6, account.getCategory());
			st.setString(7, account.getDescriptor());
			st.setString(8, oldFiscalCode);

			rowsAffected = st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return rowsAffected;
	}

	/**
	 * This function retrieve all the Account in the DB and create a List of it
	 * 
	 * @param conn the Connection object to the DB
	 * @return a List<Account> of all the accounts in the DB sorted by name
	 */
	public static List<Account> getAccounts(Connection conn) {
		String query = "select * from accounts.accounts order by \"name\"";
		List<Account> accounts = new ArrayList<Account>();

		try {
			PreparedStatement st = conn.prepareStatement(query);
			ResultSet res = st.executeQuery();
			while (res.next())
				accounts.add(new Account(res));
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		if (accounts.size() > 0)
			return accounts;
		else
			return null;
	}

	/**
	 * This function retrieve the account with the matching fiscal code passed as
	 * parameter
	 * 
	 * @param conn       the Connection object to the DB
	 * @param fiscalCode a String containing the fiscal code of the Account you're
	 *                   looking for
	 * @return an Account matching the fiscal code or null if the Account cannot be
	 *         found.
	 */
	public static Account getAccount(Connection conn, String fiscalCode) {
		String query = "select * from accounts.accounts a where a.fiscalcode = ? ";
		Account account = null;

		try {
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, fiscalCode);
			ResultSet res = st.executeQuery();
			res.next();
			account = new Account(res);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		return account;
	}

	/**
	 * This function retrieve the account with the matching VAT number passed as
	 * parameter
	 * 
	 * @param conn      the Connection object to the DB
	 * @param vatNumber a String containing the VAT number of the Account you're
	 *                  looking for
	 * @return an Account matching the VAT number or null if the Account cannot be
	 *         found
	 */
	public static Account getAccountFromVATNumber(Connection conn, String vatNumber) {
		String query = "select * from accounts.accounts a where a.numbervat = ? ";
		Account account = null;

		try {
			PreparedStatement st = conn.prepareStatement(query);
			st.setString(1, vatNumber);
			ResultSet res = st.executeQuery();
			res.next();
			account = new Account(res);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		return account;
	}

	/**
	 * This function retrieve the account of a certain Invoice
	 * 
	 * @param conn    the Connection object to the DB
	 * @param invoice the Invoice that corresponds to the Account you are looking
	 *                for.
	 * @return an Account that correspond to the Invoice or null if the Account
	 *         cannot be found
	 */
	public static Account getAccount(Connection conn, Invoice invoice) {
		String query = "select a.fiscalcode, a.\"name\", a.numbervat, a.atecocode, a.legal_address, a.customer_category, a.descriptor "
				+ "from accounts.accounts a, jobs.jobs j, invoices.invoices i, deadlines.rspp r "
				+ "where a.fiscalcode = j.customer and r.invoiceid = i.invoiceid and r.rspp_jobid = j.jobs_id and "
				+ "i.\"number\" = ? and i.\"type\" = ? and date_part('year', emission) = ? ";
		Account account = null;

		try {
			PreparedStatement st = conn.prepareStatement(query);
			st.setInt(1, invoice.getNumber());
			st.setString(2, invoice.getType());
			st.setInt(3, invoice.getEmission().getYear());

			ResultSet res = st.executeQuery();
			account = new Account(res);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		return account;
	}

	/**
	 * This function retrieve all the different categories for the class Account and
	 * create a BiMap with the acronym as key and extended version as values
	 * 
	 * @param conn the Connection object to the DB
	 * @return a BiMap that match the account categories ID to the extended naming
	 */
	public static BiMap<String, String> getAccountCategories(Connection conn) {
		String sql = "select * from accounts.accounts_categories ac ";
		BiMap<String, String> categories = HashBiMap.create();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				categories.put(res.getString("categories"), res.getString("extended"));
			}
			return categories;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
}
