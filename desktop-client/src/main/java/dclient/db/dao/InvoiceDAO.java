package dclient.db.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.google.common.base.Throwables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dclient.model.Invoice;

public class InvoiceDAO {
	private static Logger logger = LoggerFactory.getLogger("DClient");

	/**
	 * This function create a new Invoice in the database that correspond to the
	 * object passed as paramenter. It does nothing if an Invoice with the same code
	 * is already in the DB
	 * 
	 * @param conn    the Connection object to the DB
	 * @param invoice the Invoice object to be created in the DB
	 * @return the number of database row that has been changed by the function
	 */
	public static int newInvoice(Connection conn, Invoice invoice) {
		String query = "insert into invoices.invoices (invoiceid, \"number\", emission, \"type\", payed, description) values ( ? , ? , ? , ? , ? , ? ) ";
		int rowsAffected = 0;

		try (PreparedStatement st = conn.prepareStatement(query);){
			st.setString(1, invoice.getId());
			st.setInt(2, invoice.getNumber());
			st.setDate(3, Date.valueOf(invoice.getEmission()));
			st.setString(4, invoice.getType());
			st.setBoolean(5, invoice.getPayed());
			st.setString(6, invoice.getDescription());

			rowsAffected = st.executeUpdate();
		} catch (SQLException e) {
			logger.error(Throwables.getStackTraceAsString(e));
		}
		return rowsAffected;
	}

	/**
	 * This function update an existing Invoice in the DB to match the passed
	 * Invoice object. It does nothing if the Invoice doesn't exist in the DB
	 * 
	 * @param conn the Connection object to the DB
	 * @param oldInvoiceID the Invoice id to be updated in the DB
	 * @param invoice the new updated Invoice that you want on the DB
	 * @return the number of database row that has been changed by the function
	 */
	public static int updateInvoice(Connection conn, String oldInvoiceID, Invoice invoice) {
		String query = "update invoices.invoices set invoiceid = ? , \"number\" = ? , emission = ? , \"type\" = ? , payed = ?, description = ? where invoiceid = ? ";
		int rowsAffected = 0;

		try (PreparedStatement st = conn.prepareStatement(query);){
			st.setString(1, invoice.getId());
			st.setInt(2, invoice.getNumber());
			st.setDate(3, Date.valueOf(invoice.getEmission()));
			st.setString(4, invoice.getType());
			st.setBoolean(5, invoice.getPayed());
			st.setString(6, invoice.getDescription());
			st.setString(7, oldInvoiceID);

			rowsAffected = st.executeUpdate();
		} catch (SQLException e) {
			logger.error(Throwables.getStackTraceAsString(e));
		}
		return rowsAffected;
	}
}
