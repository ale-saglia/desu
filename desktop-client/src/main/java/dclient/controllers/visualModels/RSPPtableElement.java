package dclient.controllers.visualModels;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RSPPtableElement {
	private static Logger logger = LoggerFactory.getLogger("DClient");

	String jobID;
	LocalDate jobStart;

	DateTimeFormatter formatter;

	ObjectProperty<LocalDate> jobEnd;
	StringProperty accountName;
	StringProperty accountDescriptor;
	StringProperty category;
	StringProperty invoiceID;
	BooleanProperty payed;
	StringProperty note;

	public RSPPtableElement(ResultSet res, String dateFormat) {
		try {
			this.jobID = res.getString("jobid");
			this.jobStart = res.getDate("jobstart").toLocalDate();

			this.jobEnd = new SimpleObjectProperty<>(res.getDate("jobend").toLocalDate());
			this.accountName = new SimpleStringProperty(res.getString("name"));
			this.accountDescriptor = new SimpleStringProperty(res.getString("descriptor"));
			this.note = new SimpleStringProperty(res.getString("notes"));
			this.category = new SimpleStringProperty(res.getString("category"));
			this.invoiceID = new SimpleStringProperty(res.getString("invoices"));
			this.payed = new SimpleBooleanProperty(res.getBoolean("payed"));

			if (dateFormat != null)
				this.formatter = DateTimeFormatter.ofPattern(dateFormat);
			else
				this.formatter = DateTimeFormatter.ISO_DATE;
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}

	public StringProperty accountNameProperty() {
		return accountName;
	}

	public String getAccountName() {
		return accountName.get();
	}

	public StringProperty accountDescriptorProperty() {
		return accountDescriptor;
	}

	public String getAccountDescriptor() {
		return accountDescriptor.get();
	}

	public StringProperty categoryProperty() {
		return category;
	}

	public String getCategory() {
		return category.get();
	}

	public ObjectProperty<LocalDate> jobEndProperty() {
		return jobEnd;
	}

	public String getJobEnd() {
		return (jobEnd.get().format(formatter));
	}

	public LocalDate jobEndDate() {
		return jobEnd.get();
	}

	public StringProperty invoiceIDProperty() {
		return invoiceID;
	}

	public String getInvoiceID() {
		return invoiceID.get();
	}

	public BooleanProperty payedProperty() {
		return payed;
	}

	public Boolean getPayed() {
		return payed.get();
	}

	public String getJobID() {
		return jobID;
	}

	public LocalDate getJobStart() {
		return jobStart;
	}

	public StringProperty noteProperty() {
		return note;
	}

	public String getNote() {
		return note.get();
	}

	@Override
	public String toString() {
		return "RSPPtableElement [accountDescriptor=" + accountDescriptor + ", accountName=" + accountName
				+ ", category=" + category + ", formatter=" + formatter + ", invoiceID=" + invoiceID + ", jobEnd="
				+ jobEnd + ", jobID=" + jobID + ", jobStart=" + jobStart + ", note=" + note + ", payed=" + payed + "]";
	}

	
}