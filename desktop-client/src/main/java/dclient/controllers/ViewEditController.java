package dclient.controllers;

import java.time.LocalDate;

import dclient.controllers.validator.FieldsValidator;
import dclient.model.Account;
import dclient.model.Invoice;
import dclient.model.Job;
import dclient.model.JobPA;
import dclient.model.Model;
import dclient.model.RSPP;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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

	MainController mainController;

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

	public void setMainControllerRef(MainController mainController) {
		this.mainController = mainController;
	}

	public void setCombo() {
		categoryAccountCombo.getItems().setAll(model.getAccountCategories().values());
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
		if (rspp.getJob().getCustomer().getName() != null)
			nameField.setText(rspp.getJob().getCustomer().getName());
		else
			nameField.clear();

		if (rspp.getJob().getCustomer().getFiscalCode() != null)
			fiscalCodeText.setText(rspp.getJob().getCustomer().getFiscalCode());
		else
			fiscalCodeText.clear();

		if (rspp.getJob().getCustomer().getNumberVAT() != null)
			numberVATField.setText(rspp.getJob().getCustomer().getNumberVAT());
		else
			numberVATField.clear();

		categoryAccountCombo.setValue(model.getAccountCategories().get(rspp.getJob().getCustomer().getCategory()));
		atecoCodeField.setText(rspp.getJob().getCustomer().getAtecoCode());

		if (rspp.getJob().getCustomer().getLegalAddress() != null)
			addressField.setText(rspp.getJob().getCustomer().getLegalAddress());
		else
			addressField.clear();
	}

	private void setJobs() {
		if (rspp.getJob().getId() != null)
			jobCodeField.setText(rspp.getJob().getId());
		else
			jobCodeField.clear();

		categoryJobCombo.setValue(rspp.getJob().getJobCategory());
		categoryTypeCombo.setValue(rspp.getJob().getJobType());

		if (rspp.getJob().getDescription() != null)
			jobdDescriptionField.setText(rspp.getJob().getDescription());
		else
			jobdDescriptionField.clear();

		rsppNote = model.getRSPPnote(rspp.getJob().getCustomer().getFiscalCode());
		if (rsppNote != null)
			noteField.setText(rsppNote);
		else
			noteField.clear();

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
			if (Integer.toString(rspp.getInvoice().getNumber()) != null)
				invoiceNumberField.setText(Integer.toString(rspp.getInvoice().getNumber()));
			else
				invoiceNumberField.clear();
			invoiceEmissionDateField.setValue(rspp.getInvoice().getEmission());
			invoiceTypeField.setText(model.getAccountCategories().get(rspp.getInvoice().getType()));
			payedCheck.setSelected(rspp.getInvoice().getPayed());
		}
	}

	/**
	 * Check if fields has been edited, than check if fields are valid and finally
	 * update only the edited fields to the DB.
	 */
	public void updateCheck() {
		Account newAccount;
		Job newJob;
		RSPP newRSPP;
		Invoice newInvoice;

		// Check if account needs to be update
		newAccount = new Account(fiscalCodeText.getText().trim(), nameField.getText().trim(),
				numberVATField.getText().trim(), atecoCodeField.getText().trim(), addressField.getText().trim(),
				model.getAccountCategories().inverse().get(categoryAccountCombo.getValue()));
		if (!newAccount.equals(rspp.getJob().getCustomer())) {
			String error = FieldsValidator.isAccountChangeValid(newAccount);
			if (error == null) {
				model.updateAccount(rspp.getJob().getCustomer().getFiscalCode(), newAccount);
				isChanged = true;
			} else {
				warningWindows(error);
				return;
			}

		}

		// Check if job needs to be updated
		newJob = new Job(jobCodeField.getText().trim(), categoryJobCombo.getValue().trim(),
				categoryTypeCombo.getValue().trim(), jobdDescriptionField.getText().trim(), newAccount);
		if (rspp.getJob() instanceof JobPA) {
			newJob = new JobPA(newJob, cigField.getText(), Integer.parseInt(decreeNumberField.getText()),
					decreeDateField.getValue());
		}
		if (!newJob.equals(rspp.getJob())) {
			String error = FieldsValidator.isJobChangeValid(newJob);
			if (error == null) {
				model.updateJob(rspp.getJob().getId(), newJob);
				isChanged = true;
			} else {
				warningWindows(error);
				return;
			}

		}

		// Check if rspp note needs to be updated
		if (!noteField.getText().trim().equals(rsppNote)) {
			String error = FieldsValidator.isRSPPNoteChangeValid(rsppNote);
			if (error == null) {
				model.updateNote(fiscalCodeText.getText(), noteField.getText());
				isChanged = true;
			} else {
				warningWindows(error);
				return;
			}

		}

		// Check if invoice needs to be updated
		String oldInvoiceID = null;
		Integer invoiceNumber = null;

		if (!invoiceNumberField.getText().trim().isEmpty())
			invoiceNumber = Integer.parseInt(invoiceNumberField.getText().trim());
		if (rspp.getInvoice() != null)
			oldInvoiceID = rspp.getInvoice().getId();
		newInvoice = new Invoice(invoiceNumber, invoiceEmissionDateField.getValue(), newAccount.getCategory(),
				payedCheck.isSelected());

		if (!newInvoice.equals(rspp.getInvoice()) && newInvoice.getId() != null) {
			String error = FieldsValidator.isInvoiceChangeValid(newInvoice);
			if (error == null) {
				model.updateInvoice(oldInvoiceID, newInvoice, rspp);
				isChanged = true;
			} else {
				warningWindows(error);
				return;
			}

		}

		// Check if RSPP needs to be updated
		newRSPP = new RSPP(newJob, jobStartField.getValue(), jobEndField.getValue(), newInvoice);
		if (!newRSPP.equals(rspp) && newInvoice.getId() != null) {
			String error = FieldsValidator.isRSPPChangeValid(newRSPP);
			if (error == null) {
				model.updateRSPP(rspp.getJob().getId(), rspp.getStart(), newRSPP);
				isChanged = true;
			} else {
				warningWindows(error);
				return;
			}

		}

		if (isChanged) {
			System.out.println("Some elements were modified");
			mainController.refresh();
		}
		closeButtonAction();
	}

	private void warningWindows(String message) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Attenzione");
		alert.setHeaderText("Sono stati rilevati i seguenti campi non validi:");
		alert.setContentText(message);

		// TODO refresh changed fields
		alert.showAndWait();
	}
}