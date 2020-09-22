package dclient.db.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import dclient.model.Invoice;
import dclient.model.Rspp;

public class InvoiceDAO {
	public static Collection<Invoice> getInvoices(Connection conn, Rspp rspp) {
		String query = "select i.* from( select invoice_id from deadlines.rspp_invoices ri where rspp_id = ? and rspp_start = ?) as vi, invoices.invoices i where vi.invoice_id = i.invoiceid";
		Set<Invoice> invoicesMap = new TreeSet<Invoice>();

		try {
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, rspp.getJob().getId());
			st.setDate(2, Date.valueOf(rspp.getStart()));

			ResultSet res = st.executeQuery();
			
			while(res.next())
				invoicesMap.add(new Invoice(res));
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return invoicesMap;
	}
	
	public static int newInvoice(Connection conn, Invoice invoice) {
		String query = "insert into invoices.invoices (invoiceid, \"number\", emission, \"type\", payed, description) values ( ? , ? , ? , ? , ? , ? ) ";
		int rowsAffected = 0;

		try {
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, invoice.getId());
			st.setInt(2, invoice.getNumber());
			st.setDate(3, Date.valueOf(invoice.getEmission()));
			st.setString(4, invoice.getType());
			st.setBoolean(5, invoice.getPayed());
			st.setString(6, invoice.getDescription());

			rowsAffected = st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		System.out.println("Rows updated INVOICE => " + rowsAffected);
		return rowsAffected;
	}

	public static int updateInvoice(Connection conn, String oldInvoiceID, Invoice invoice) {
		String query = "update invoices.invoices set invoiceid = ? , \"number\" = ? , emission = ? , \"type\" = ? , payed = ?, description = ? where invoiceid = ? ";
		int rowsAffected = 0;

		try {
			PreparedStatement st = conn.prepareStatement(query);

			st.setString(1, invoice.getId());
			st.setInt(2, invoice.getNumber());
			st.setDate(3, Date.valueOf(invoice.getEmission()));
			st.setString(4, invoice.getType());
			st.setBoolean(5, invoice.getPayed());
			st.setString(6, invoice.getDescription());
			st.setString(7, oldInvoiceID);

			rowsAffected = st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		System.out.println("Rows updated INVOICE => " + rowsAffected);
		return rowsAffected;
	}
}
