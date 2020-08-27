package dclient.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.BiMap;

import dclient.model.Account;
import dclient.model.Invoice;
import dclient.model.Job;
import dclient.model.JobPA;
import dclient.model.Model;
import dclient.model.RSPP;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewEditController {
	private Model model;
	private RSPP rspp;
	private String rsppNote;

	private BiMap<String, String> accountCategories;

	boolean isChanged;

	@FXML
	private TextField nameField;

	@FXML
	private TextField fiscalCodeText;

	@FXML
	private TextField numberVATField;

	@FXML
	private ComboBox<String> categoryAccountCombo;

	@FXML
	private TextField atecoCodeField;

	@FXML
	private TextField addressField;

	@FXML
	private TextField jobCodeField;

	@FXML
	private TextField jobdDescriptionField;

	@FXML
	private ComboBox<String> categoryJobCombo;

	@FXML
	private ComboBox<String> categoryTypeCombo;

	@FXML
	private VBox paReferences;

	@FXML
	private TextField cigField;

	@FXML
	private TextField decreeNumberField;

	@FXML
	private DatePicker decreeDateField;

	@FXML
	private TextArea noteField;

	@FXML
	private DatePicker jobStartField;

	@FXML
	private DatePicker jobEndField;

	@FXML
	private TextField invoiceNumberField;

	@FXML
	private DatePicker invoiceEmissionDateField;

	@FXML
	private TextField invoiceTypeField;

	@FXML
	private CheckBox payedCheck;

	@FXML
	private Button closeButton;

	@FXML
	private void closeButtonAction() {
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public void setCombo() {
		accountCategories = model.getAccountCategories();
		categoryAccountCombo.getItems().setAll(accountCategories.values());
		categoryJobCombo.getItems().setAll(model.getJobCategories());
		categoryTypeCombo.getItems().setAll(model.getJobTypes());
	}

	public void setRSPP(String jobID, LocalDate jobStart) {
		rspp = model.getRSPP(jobID, jobStart);

		setAnagrafica();
		setJobs();
		setRSPP();
		setInvoice();

		isChanged = false;
	}

	private void setAnagrafica() {
		nameField.setText(rspp.getJob().getCustomer().getName());
		fiscalCodeText.setText(rspp.getJob().getCustomer().getFiscalCode());
		numberVATField.setText(rspp.getJob().getCustomer().getNumberVAT());
		categoryAccountCombo.setValue(model.getAccountCategories().get(rspp.getJob().getCustomer().getCategory()));
		atecoCodeField.setText(rspp.getJob().getCustomer().getAtecoCode());
		addressField.setText(rspp.getJob().getCustomer().getLegalAddress());
	}

	private void setJobs() {
		jobCodeField.setText(rspp.getJob().getId());
		categoryJobCombo.setValue(rspp.getJob().getJobCategory());
		categoryTypeCombo.setValue(rspp.getJob().getJobType());
		jobdDescriptionField.setText(rspp.getJob().getDescription());
		noteField.setText(rsppNote = model.getRSPPnote(rspp.getJob().getCustomer().getFiscalCode()));

		if (rspp.getJob() instanceof JobPA) {
			JobPA jobPA = (JobPA) rspp.getJob();
			cigField.setText(jobPA.getCig());
			decreeNumberField.setText(Integer.toString(jobPA.getDecreeNumber()));
			decreeDateField.setValue(jobPA.getDecreeDate());
		} else
			paReferences.getChildren().clear();
	}

	private void setRSPP() {
		jobStartField.setValue(rspp.getStart());
		jobEndField.setValue(rspp.getEnd());
	}

	private void setInvoice() {
		if (rspp.getInvoice() != null) {
			invoiceNumberField.setText(Integer.toString(rspp.getInvoice().getNumber()));
			invoiceEmissionDateField.setValue(rspp.getInvoice().getEmission());
			invoiceTypeField.setText(model.getAccountCategories().get(rspp.getInvoice().getType()));
			payedCheck.setSelected(rspp.getInvoice().getPayed());
		}
	}

	// TODO fire randomly update even if not needed
	public void updateCheck() {
		Account newAccount;
		Job newJob;
		RSPP newRSPP;
		Invoice newInvoice;

		// Check if account needs to be updated
		newAccount = new Account(fiscalCodeText.getText(), nameField.getText(), numberVATField.getText(),
				atecoCodeField.getText(), addressField.getText(),
				accountCategories.inverse().get(categoryAccountCombo.getValue()));
		if (newAccount.equals(rspp.getJob().getCustomer())) {
			updateAccounts();
			isChanged = true;
		}

		// Check if job needs to be updated
		newJob = new Job(jobCodeField.getText(), categoryJobCombo.getValue(), categoryTypeCombo.getValue(),
				jobdDescriptionField.getText(), newAccount);
		if (rspp.getJob() instanceof JobPA) {
			newJob = new JobPA(newJob, cigField.getText(), Integer.parseInt(decreeNumberField.getText()),
					decreeDateField.getValue());
		}
		if (!newJob.equals(rspp.getJob())) {
			updateJob();
			isChanged = true;
		}

		// Check if rspp note needs to be updated
		if (noteField.getText() != rsppNote) {
			updateNote();
			isChanged = true;
		}

		// Check if invoice needs to be updated
		newInvoice = new Invoice(Integer.parseInt(invoiceNumberField.getText()), invoiceEmissionDateField.getValue(),
				newAccount.getCategory(), payedCheck.isSelected());
		if (!newInvoice.equals(newInvoice)) {
			updateInvoice();
			isChanged = true;
		}

		// Check if RSPP needs to be updated
		newRSPP = new RSPP(newJob, jobStartField.getValue(), jobEndField.getValue(), newInvoice);
		if (!newRSPP.equals(rspp)) {
			updateRSPP();
			isChanged = true;
		}

		closeButtonAction();
	}

	private void updateAccounts() {
		String oldFiscalCode = rspp.getJob().getCustomer().getFiscalCode();
		Map<String, Object> data = new HashMap<String, Object>();

		data.put("fiscalCode", fiscalCodeText.getText());
		data.put("name", nameField.getText());
		data.put("numberVAT", numberVATField.getText());
		data.put("atecoCode", atecoCodeField.getText());
		data.put("legaAddress", addressField.getText());
		data.put("customerCategory", accountCategories.inverse().get(categoryAccountCombo.getValue()));

		model.updateAccount(oldFiscalCode, data);
	}

	private void updateJob() {
		String oldJobCode = rspp.getJob().getId();
		Map<String, Object> data = new HashMap<String, Object>();

		data.put("jobCode", jobCodeField.getText());
		data.put("category", categoryJobCombo.getValue());
		data.put("type", categoryTypeCombo.getValue());
		data.put("description", jobdDescriptionField.getText());

		if (rspp.getJob() instanceof JobPA) {
			data.put("cig", cigField.getText());
			data.put("decreeNumber", decreeNumberField.getText());
			data.put("decreeDate", decreeDateField.getValue());
		}

		model.updateJob(oldJobCode, data);
	}

	private void updateNote() {
		String accountID = fiscalCodeText.getText();
		String note = noteField.getText();

		model.updateNote(accountID, note);
	}

	private void updateRSPP() {
		String jobCode = jobCodeField.getText();
		LocalDate oldJobStart = rspp.getStart();
		Map<String, Object> data = new HashMap<String, Object>();

		data.put("jobStart", rspp.getStart());
		data.put("jobEnd", rspp.getEnd());

		model.updateRSPP(jobCode, oldJobStart, data);
	}

	private void updateInvoice() {
		String oldInvoiceID = invoiceNumberField.getText();
		String jobID = jobCodeField.getText();
		Map<String, Object> data = new HashMap<String, Object>();

		data.put("invoiceNumber", invoiceNumberField.getText());
		data.put("invoiceEmissionDate", invoiceEmissionDateField.getValue());
		data.put("type", categoryAccountCombo.getValue());
		data.put("payed", payedCheck.isSelected());

		model.updateInvoice(oldInvoiceID, jobID, data);
	}
}