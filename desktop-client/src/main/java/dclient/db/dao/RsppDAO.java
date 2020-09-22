package dclient.db.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;

import dclient.controllers.visualModels.RSPPtableElement;
import dclient.db.ConnectDB;
import dclient.model.Account;
import dclient.model.Invoice;
import dclient.model.Rspp;

public class RsppDAO {
	/**
	 * This function is meant to be a faster and efficent way to retrieve all te
	 * data that is shown in the main view of the program
	 * 
	 * @param conn the Connection object to the DB
	 * @param config the configuration file of the program (usually find under the .dclient folder in user settings)
	 * @return a list of RSPPtableElement to be shown on the main view of the program
	 */
	public static List<RSPPtableElement> getRSPPTable(Connection conn, Properties config) {
		String sql;
		List<RSPPtableElement> tableElements = new LinkedList<RSPPtableElement>();
		try {
			sql = Resources.toString(ConnectDB.class.getResource("dclient/db/mainViewQuery.sql"), Charsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next())
				tableElements.add(new RSPPtableElement(res, config.getProperty("dateFormat", null)));
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		return tableElements;
	}

	/**
	 * @param conn
	 * @param rspp
	 * @return
	 */
	public static int newRSPP(Connection conn, Rspp rspp) {
		String query = "insert into deadlines.rspp (rspp_jobid, jobstart, jobend) values (?, ?, ?)";
		int rowsAffected = 0;

		try {
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, rspp.getJob().getId());
			st.setDate(2, Date.valueOf(rspp.getStart()));
			st.setDate(3, Date.valueOf(rspp.getEnd()));

			rowsAffected = st.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database o campo già esistente");
			return -1;
		}

		System.out.println("Rows updated ACCOUNT => " + rowsAffected);
		return 0;
	}

	public static int updateRSPP(Connection conn, String jobCode, LocalDate oldJobStart, Rspp rspp) {
		String query = "update deadlines.rspp set jobstart = ? , jobend = ? "
				+ "where rspp_jobid = ? and jobstart = ? ";

		int rowsAffected = 0;

		try {
			PreparedStatement st = conn.prepareStatement(query);

			st.setDate(1, Date.valueOf(rspp.getStart()));
			st.setDate(2, Date.valueOf(rspp.getEnd()));
			st.setString(3, jobCode);
			st.setDate(4, Date.valueOf(oldJobStart));

			rowsAffected = st.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return rowsAffected;
	}

	public static Collection<Rspp> getRSPPs(Connection conn) {
		String sql = "select * from deadlines.rspp";
		List<Rspp> rsppList = new LinkedList<Rspp>();

		try {
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();

			while (res.next()) {
				Rspp rspp = new Rspp(conn, res);
				rsppList.add(rspp);
			}
			return rsppList;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public static Collection<Rspp> getRSPPs(Connection conn, Account account) {
		String sql = "select * from deadlines.rspp r, jobs.jobs j where r.rspp_jobid = j.jobs_id and customer = ? ";
		List<Rspp> rsppList = new LinkedList<Rspp>();

		try {
			PreparedStatement st = conn.prepareStatement(sql);

			st.setString(1, account.getFiscalCode());

			ResultSet res = st.executeQuery();

			while (res.next()) {
				Rspp rspp = new Rspp(conn, res);
				rsppList.add(rspp);
			}
			return rsppList;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public static Rspp getRSPP(Connection conn, Invoice invoice) {
		String query = "select r.rspp_jobid, r.jobstart, r.jobend from deadlines.rspp r, deadlines.rspp_invoices ri where r.rspp_jobid = ri.rspp_id and r.jobstart = ri.rspp_start and ri.invoice_id = ? ";
		Rspp rspp = null;

		try {
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, invoice.getId());

			ResultSet res = st.executeQuery();
			if (res.next() == false)
				return null;
			rspp = new Rspp(conn, res);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return rspp;
	}

	public static Rspp getRSPP(Connection conn, String jobID, LocalDate startJob) {
		String sql = "select * from deadlines.rspp r where r.rspp_jobid = ? and r.jobstart = ? ";
		Rspp rspp;

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, jobID);
			st.setDate(2, Date.valueOf(startJob));

			ResultSet res = st.executeQuery();
			res.next();
			rspp = new Rspp(conn, res);
			return rspp;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public static Rspp getLastRSPP(Connection conn, Account account) {
		String sql = "select * from deadlines.rspp r, jobs.jobs j where r.rspp_jobid = j.jobs_id and customer = ? order by jobend desc LIMIT 1 ";
		Rspp rspp = null;

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, account.getFiscalCode());

			ResultSet res = st.executeQuery();
			if (res.next() == false)
				return null;
			rspp = new Rspp(conn, res);
			return rspp;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

	}

	public static int updateNote(Connection conn, String accountID, String note) {
		String query = "INSERT INTO deadlines.rspp_notes (fiscalcode, notes) VALUES ( ? , ? ) ON CONFLICT (fiscalcode) DO UPDATE SET notes = ? ";

		int rowsAffected = 0;

		try {
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, accountID);
			st.setString(2, note);
			st.setString(3, note);

			rowsAffected = st.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		return rowsAffected;
	}

	public static String getRSPPnote(Connection conn, String fiscalCode) {
		String sql = "select * from deadlines.rspp_notes r where r.fiscalcode = ? ";
		String notes;

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, fiscalCode);

			ResultSet res = st.executeQuery();
			if (res.next() == false)
				return "";
			else {
				notes = res.getString("notes");
				return notes;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public static int matchRSPPInvoice(Connection conn, Rspp rspp, Invoice invoice) {
		String query = "insert into deadlines.rspp_invoices (rspp_id, rspp_start, invoice_id) values ( ? , ? , ? ) ";
		int rowsAffected = 0;

		try {
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, rspp.getJob().getId());
			st.setDate(2, Date.valueOf(rspp.getStart()));
			st.setString(3, invoice.getId());

			rowsAffected = st.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		System.out.println("Rows updated JOBS_INVOICE => " + rowsAffected);
		return rowsAffected;
	}

	public static int updateInvoiceMonths(Connection conn, Rspp rspp, Set<Integer> invoiceMonths) {
		String query = "INSERT INTO deadlines.rspp_invoices_months (customer, months) VALUES ( ? , ? ) ON CONFLICT (customer) DO UPDATE SET months = ? ";
		int rowsAffected = 0;

		try {
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, rspp.getJob().getCustomer().getFiscalCode());
			st.setArray(2, conn.createArrayOf("INTEGER", invoiceMonths.toArray()));
			st.setArray(3, conn.createArrayOf("INTEGER", invoiceMonths.toArray()));

			rowsAffected = st.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return rowsAffected;
	}

	public static Set<Integer> getInvoiceMonths(Connection conn, Rspp rspp) {
		String sql = "select * from deadlines.rspp_invoices_months rim where customer = ? ";
		Set<Integer> invoiceMonths = null;

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, rspp.getJob().getCustomer().getFiscalCode());

			ResultSet res = st.executeQuery();
			if (res.next() == false)
				return null;
			else {
				invoiceMonths = Sets.newHashSet(((Integer[]) res.getArray("months").getArray()));
			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		if (invoiceMonths == null || invoiceMonths.size() <= 0)
			return null;
		else
			return invoiceMonths;
	}

	/**
	 * This function retrieve all Invoice object related to a specific RSPP
	 * 
	 * @param conn
	 * @param rspp
	 * @return
	 */
	public static Collection<Invoice> getInvoices(Connection conn, Rspp rspp) {
		String query = "select i.* from( select invoice_id from deadlines.rspp_invoices ri where rspp_id = ? and rspp_start = ?) as vi, invoices.invoices i where vi.invoice_id = i.invoiceid";
		Set<Invoice> invoicesMap = new TreeSet<Invoice>();

		try {
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, rspp.getJob().getId());
			st.setDate(2, Date.valueOf(rspp.getStart()));

			ResultSet res = st.executeQuery();

			while (res.next())
				invoicesMap.add(new Invoice(res));
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return invoicesMap;
	}
}
