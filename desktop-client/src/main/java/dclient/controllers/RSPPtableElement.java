package dclient.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import dclient.model.Model;
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

	public RSPPtableElement(final Map<String, String> rsspElement, Model model) {
		this.jobID = rsspElement.get("jobid");
		this.jobStart = LocalDate.parse(rsspElement.get("jobstart"));
		this.jobEnd = new SimpleObjectProperty<LocalDate>(LocalDate.parse(rsspElement.get("jobend")));
		this.accountName = new SimpleStringProperty(rsspElement.get("name"));
		this.note = new SimpleStringProperty(rsspElement.get("note"));
		this.category = new SimpleStringProperty(model.getAccountCategories().get(rsspElement.get("category")));
		this.invoiceID = new SimpleStringProperty(rsspElement.get("invoiceid"));
		this.payed = new SimpleBooleanProperty(rsspElement.get("payed").contains("true"));
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