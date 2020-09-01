package dclient.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import dclient.controllers.visualModels.RSPPtableElement;
import dclient.model.Account;
import dclient.model.Invoice;
import dclient.model.Job;
import dclient.model.JobPA;
import dclient.model.RSPP;

public class SicurteaDAO {
	int daysAdvance;

	Properties config;
	Session session;

	public SicurteaDAO(Properties config) {
		this.config = config;
		daysAdvance = Integer.parseInt(config.getProperty("rsppTable.daysAdvance", "14"));
		this.session = ConnectSSH.getSession(config);
	}

	public List<RSPPtableElement> getDataForTable() {
		String sql = ("SELECT jobid,\n" + "       jobstart,\n" + "       jobend,\n" + "       \"name\",\n"
				+ "       category,\n" + "       n2.invoiceid,\n" + "       payed,\n" + "       notes\n"
				+ "FROM   (SELECT jobid,\n" + "               jobstart,\n" + "               jobend,\n"
				+ "               \"name\",\n" + "               category,\n" + "               invoiceid,\n"
				+ "               notes\n" + "        FROM   (SELECT r.rspp_jobid        AS jobid,\n"
				+ "                       r.jobstart          AS jobstart,\n"
				+ "                       r.jobend          AS jobend,\n"
				+ "                       j.customer          AS fiscalcode,\n"
				+ "                       a.\"name\"            AS \"name\",\n"
				+ "                       a.customer_category AS category,\n"
				+ "                       r.invoiceid         AS invoiceid\n"
				+ "                FROM   accounts.accounts a,\n" + "                       deadlines.rspp r,\n"
				+ "                       jobs.jobs j\n" + "                WHERE  r.rspp_jobid = j.jobs_id\n"
				+ "                       AND j.customer = a.fiscalcode\n" + "                ORDER  BY \"name\",\n"
				+ "                          jobstart) AS n1\n" + "               LEFT JOIN deadlines.rspp_notes rn\n"
				+ "                      ON n1.fiscalcode = rn.fiscalcode) AS n2\n"
				+ "       LEFT JOIN invoices.invoices i\n" + "              ON i.invoiceid = n2.invoiceid ");

		List<RSPPtableElement> tableElements = new LinkedList<RSPPtableElement>();

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();

			while (res.next()) {
				tableElements.add(new RSPPtableElement(res.getString("name"), res.getString("category"),
						res.getDate("jobend").toLocalDate(), res.getString("invoiceid"), res.getBoolean("payed"),
						res.getString("notes"), res.getString("jobid"), res.getDate("jobstart").toLocalDate()));
			}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return tableElements;
	}

	public BiMap<String, String> getAccountsCategories() {
		String sql = "select * from accounts.accounts_categories ac ";
		BiMap<String, String> categories = HashBiMap.create();

		try {
			Connection conn = ConnectDB.getConnection(session, config);
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

	public List<String> getJobCategories() {
		String sql = "select * from jobs.jobs_categories jc";
		List<String> categories = new LinkedList<String>();

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();
			while (res.next()) {
				categories.add(res.getString("categories"));
			}

			return categories;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<String> getJobTypes() {
		String sql = "select * from jobs.job_types jt ";
		List<String> types = new LinkedList<String>();

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();
			while (res.next()) {
				types.add(res.getString("types"));
			}

			return types;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public RSPP getRSPP(String jobID, LocalDate startJob) {
		String sql = "select * from deadlines.rspp r where r.rspp_jobid = ? and r.jobstart = ? ";
		RSPP rspp;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, jobID);
			st.setDate(2, Date.valueOf(startJob));

			ResultSet res = st.executeQuery();
			res.next();
			rspp = new RSPP(getJob(res.getString("rspp_jobid"), conn), res.getDate("jobstart").toLocalDate(),
					res.getDate("jobend").toLocalDate(), getInvoice(res.getString("invoiceid"), conn));
			conn.close();
			return rspp;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<RSPP> getRSPPList() {
		String sql = "select * from deadlines.rspp";
		List<RSPP> rspp = new LinkedList<RSPP>();

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();

			while (res.next()) {
				rspp.add(new RSPP(getJob(res.getString("rspp_jobid"), conn), res.getDate("jobstart").toLocalDate(),
						res.getDate("jobend").toLocalDate(), getInvoice(res.getString("invoiceid"), conn)));
			}
			conn.close();
			return rspp;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	private Job getJob(String job_id, Connection conn) {
		String sql = "select * from jobs.jobs j where j.jobs_id = ? ";
		Job job;

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, job_id);

			ResultSet res = st.executeQuery();
			res.next();
			job = new Job(res.getString("jobs_id"), res.getString("jobs_category"), res.getString("jobs_type"),
					res.getString("jobs_description"), getAccount(res.getString("customer"), conn));

			if (job.getCustomer().getCategory().contains("pa")) {
				sql = "select * from jobs.jobs_pa where job_id = ? ";
				st = conn.prepareStatement(sql);
				st.setString(1, job.getId());
				res = st.executeQuery();
				res.next();
				job = new JobPA(job, res.getString("cig"), res.getInt("decree_number"),
						res.getDate("decree_date").toLocalDate());
			}

			return job;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	private Invoice getInvoice(String invoiceID, Connection conn) {
		String sql = "select * from invoices.invoices i where invoiceid = ? ";
		Invoice invoice;

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, invoiceID);

			ResultSet res = st.executeQuery();

			if (res.next() != false) {
				invoice = new Invoice(res.getString("invoiceid"), res.getInt("number"),
						res.getDate("emission").toLocalDate(), res.getString("type"), res.getBoolean("payed"));
				return invoice;
			}

			return null;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	private Account getAccount(String fiscalCode, Connection conn) {
		String sql = "select * from accounts.accounts a where a.fiscalcode = ? ";
		Account account;

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, fiscalCode);

			ResultSet res = st.executeQuery();
			res.next();
			account = new Account(res.getString("fiscalcode"), res.getString("name"), res.getString("numbervat"),
					res.getString("atecocode"), res.getString("legal_address"), res.getString("customer_category"));
			return account;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public Account getAccount(String fiscalCode) {
		String sql = "select * from accounts.accounts a where a.fiscalcode = ? ";
		Account account = null;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, fiscalCode);

			ResultSet res = st.executeQuery();

			if (res.next()) {
				account = new Account(res.getString("fiscalcode"), res.getString("name"), res.getString("numbervat"),
						res.getString("atecocode"), res.getString("legal_address"), res.getString("customer_category"));
			}
			return account;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public String getRSPPnote(String fiscalCode) {
		String sql = "select * from deadlines.rspp_notes r where r.fiscalcode = ? ";
		String notes;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, fiscalCode);

			ResultSet res = st.executeQuery();
			conn.close();
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

	public Session getSession() {
		return session;
	}

	public void getNewSession() {
		this.session = ConnectSSH.getSession(config);
	}

	public String closeSession() {
		String message = null;
		try {
			message = "Closing: " + session.getPortForwardingL();
		} catch (JSchException e) {
			e.printStackTrace();
		}
		this.session.disconnect();
		return message;
	}

	public void updateAccount(String oldFiscalCode, Account account) {
		String query = "UPDATE accounts.accounts "
				+ "SET fiscalcode = ?, \"name\" = ?, numbervat = ?, atecocode = ?, legal_address = ?, customer_category = ? "
				+ "WHERE fiscalcode = ? ";

		int rowsAffected = 0;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, account.getFiscalCode());
			st.setString(2, account.getName());
			st.setString(3, account.getNumberVAT());
			st.setString(4, account.getAtecoCode());
			st.setString(5, account.getLegalAddress());
			st.setString(6, account.getCategory());
			st.setString(7, oldFiscalCode);

			rowsAffected = st.executeUpdate();

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		System.out.println("Rows updated ACCOUNT => " + rowsAffected);
	}

	public void updateJob(String oldJobCode, Job job) {
		String query = "update jobs.jobs " + "set jobs_id = ?, jobs_category = ?, jobs_type = ?, jobs_description = ? "
				+ "where jobs_id = ? ";

		int rowsAffected = 0;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, job.getId());
			st.setString(2, job.getJobCategory());
			st.setString(3, job.getJobType());
			st.setString(4, job.getDescription());
			st.setString(5, oldJobCode);

			rowsAffected = st.executeUpdate();

			if (job instanceof JobPA) {
				query = "update jobs.jobs_pa \r\n"
						+ "set cig = ? , jobs_category = ? , decree_number = ? , decree_date = ? \r\n"
						+ "where job_id = ? ";

				st = conn.prepareStatement(query);

				st.setString(1, ((JobPA) job).getCig());
				st.setInt(2, ((JobPA) job).getDecreeNumber());
				st.setDate(3, Date.valueOf(((JobPA) job).getDecreeDate()));
				st.setString(4, job.getId());

				st.executeUpdate();

				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		System.out.println("Rows updated JOBS => " + rowsAffected);

	}

	public void updateNote(String accountID, String note) {
		String query = "INSERT INTO deadlines.rspp_notes " + "(fiscalcode, notes) VALUES ( ? , ? ) "
				+ "ON CONFLICT (fiscalcode) DO UPDATE SET notes = ? ";

		int rowsAffected = 0;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, accountID);
			st.setString(2, note);
			st.setString(3, note);

			rowsAffected = st.executeUpdate();

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		System.out.println("Rows updated NOTES => " + rowsAffected);
	}

	public void updateRSPP(String jobCode, LocalDate oldJobStart, RSPP rspp) {
		String query = "update deadlines.rspp set jobstart = ? , jobend = ? "
				+ "where rspp_jobid = ? and jobstart = ? ";

		int rowsAffected = 0;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setDate(1, Date.valueOf(rspp.getStart()));
			st.setDate(2, Date.valueOf(rspp.getEnd()));
			st.setString(3, jobCode);
			st.setDate(4, Date.valueOf(oldJobStart));

			rowsAffected = st.executeUpdate();

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		System.out.println("Rows updated RSPP => " + rowsAffected);
	}

	public void updateInvoice(String oldInvoiceID, Invoice invoice, RSPP rspp) {
		int rowsAffected = 0;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st;

			if (oldInvoiceID == null || oldInvoiceID.isEmpty()) {
				// Case add new invoice
				st = conn.prepareStatement(
						"insert into invoices.invoices (invoiceid, \"number\", emission, \"type\", payed) values ( ? , ? , ? , ? , ? )");
				st.setString(1, invoice.getId());
				st.setInt(2, invoice.getNumber());
				st.setDate(3, Date.valueOf(invoice.getEmission()));
				st.setString(4, invoice.getType());
				st.setBoolean(5, invoice.getPayed());
				rowsAffected = st.executeUpdate();

			} else {
				// Case edit invoice
				st = conn.prepareStatement(
						"update invoices.invoices set invoiceid = ? , \"number\" = ? , emission = ? , \"type\" = ? , payed = ? "
								+ "where invoiceid = ? ");
				st.setString(1, invoice.getId());
				st.setInt(2, invoice.getNumber());
				st.setDate(3, Date.valueOf(invoice.getEmission()));
				st.setString(4, invoice.getType());
				st.setBoolean(5, invoice.getPayed());
				st.setString(6, oldInvoiceID);
				rowsAffected = st.executeUpdate();
			}

			st = conn
					.prepareStatement("update deadlines.rspp set invoiceid = ? where rspp_jobid = ? and jobstart = ? ");
			st.setString(1, invoice.getId());
			st.setString(2, rspp.getJob().getId());
			st.setDate(3, Date.valueOf(rspp.getStart()));
			rowsAffected += st.executeUpdate();

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		System.out.println("Rows updated INVOICE => " + rowsAffected);
	}

	public int newAccount(Account account) {
		String query = "insert into accounts.accounts "
				+ "(fiscalcode,\"name\",numbervat,atecocode,legal_address,customer_category) "
				+ "values ( ? , ? , ? , ? , ? , ? ) ";

		int rowsAffected = 0;

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, account.getFiscalCode());
			st.setString(2, account.getName());
			st.setString(3, account.getNumberVAT());
			st.setString(4, account.getAtecoCode());
			st.setString(5, account.getLegalAddress());
			st.setString(6, account.getCategory());

			rowsAffected = st.executeUpdate();

			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database o campo già esistente");
			return -1;
		}

		System.out.println("Rows updated ACCOUNT => " + rowsAffected);
		return 0;
	}

	public List<Account> getAllAccounts() {
		String sql = "select * from accounts.accounts order by \"name\"";
		List<Account> accounts = new LinkedList<Account>();

		try {
			Connection conn = ConnectDB.getConnection(session, config);
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet res = st.executeQuery();

			while (res.next()) {
				accounts.add(new Account(res.getString("fiscalcode"), res.getString("name"), res.getString("numbervat"),
						res.getString("atecocode"), res.getString("legal_address"),
						res.getString("customer_category")));
			}
			return accounts;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Job> getAllJobOfAccount(Account account) {
		List<Job> jobs = new LinkedList<Job>();
			try {
				Connection conn = ConnectDB.getConnection(session, config);
				PreparedStatement st = conn.prepareStatement(
						"select j.jobs_id, jobs_category, jobs_type, jobs_description, cig, decree_number, decree_date from jobs.jobs j left join jobs.jobs_pa jp on j.jobs_id = jp.job_id where customer = ? ");

				st.setString(1, account.getFiscalCode());
				ResultSet res = st.executeQuery();

				while (res.next()) {
					Job job = new Job(res.getString("jobs_id"), res.getString("jobs_category"),
							res.getString("jobs_type"), res.getString("jobs_description"), account);

					if (account.getCategory().equals("pa")) {
						job = new JobPA(job, res.getString("cig"), res.getInt("decree_number"),
								res.getDate("decree_date").toLocalDate());
					}
					jobs.add(job);
				}
				return jobs;

			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Errore connessione al database");
			}
		return null;
	}
}