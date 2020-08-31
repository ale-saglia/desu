package dclient.controllers.visualModels;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RSPPtableElement {
	String jobID;
	LocalDate jobStart;
	ObjectProperty<LocalDate> jobEnd;
	StringProperty accountName;
	StringProperty category;
	StringProperty invoiceID;
	BooleanProperty payed;
	StringProperty note;

	public RSPPtableElement(String name, String category, LocalDate jobEnd, String invoiceID, Boolean payed,
			String note, String jobID, LocalDate jobStart) {
		this.jobID = jobID;
		this.jobStart = jobStart;

		this.jobEnd = new SimpleObjectProperty<LocalDate>(jobEnd);
		this.accountName = new SimpleStringProperty(name);
		this.note = new SimpleStringProperty(note);
		this.category = new SimpleStringProperty(category);
		this.invoiceID = new SimpleStringProperty(category);
		this.payed = new SimpleBooleanProperty(payed);
	}

	public StringProperty accountNameProperty() {
		return accountName;
	}

	public String getAccountName() {
		return accountName.get();
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
		return (jobEnd.get().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
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
}