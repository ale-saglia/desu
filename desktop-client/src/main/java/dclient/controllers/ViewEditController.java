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

	/**
	 * Check if fields has been edited, than check if fields are valid and finally
	 * update only the edited fields to the DB.
	 */
	public void updateCheck() {
		Account newAccount;
		Job newJob;
		RSPP newRSPP;
		Invoice newInvoice;

		// Check if account needs to be updated
		newAccount = new Account(fiscalCodeText.getText(), nameField.getText(), numberVATField.getText(),
				atecoCodeField.getText(), addressField.getText(),
				model.getAccountCategories().inverse().get(categoryAccountCombo.getValue()));
		if (!newAccount.equals(rspp.getJob().getCustomer())) {
			String error = FieldsValidator.isAccountChangeValid(model, newAccount);
			if (error == null) {
				model.updateAccount(rspp.getJob().getCustomer().getFiscalCode(), newAccount);
				isChanged = true;
			} else {
				warningWindows(error);
				return;
			}

		}

		// Check if job needs to be updated
		newJob = new Job(jobCodeField.getText(), categoryJobCombo.getValue(), categoryTypeCombo.getValue(),
				jobdDescriptionField.getText(), newAccount);
		if (rspp.getJob() instanceof JobPA) {
			newJob = new JobPA(newJob, cigField.getText(), Integer.parseInt(decreeNumberField.getText()),
					decreeDateField.getValue());
		}
		if (!newJob.equals(rspp.getJob())) {
			String error = FieldsValidator.isAccountChangeValid(model, newAccount);
			if (isJobChangeValid()) {
				model.updateJob(rspp.getJob().getId(), newJob);
				isChanged = true;
			} else {
				warningWindows("Attenzione, i valori inseriti per la pratica non sono validi, si prega di verificarli");
				return;
			}

		}

		// Check if rspp note needs to be updated
		if (!noteField.getText().equals(rsppNote)) {
			if (isNoteChangeValid()) {
				model.updateNote(fiscalCodeText.getText(), noteField.getText());
				isChanged = true;
			} else {
				warningWindows(
						"Attenzione, i valori inseriti per la nota dell'RSPP non sono validi, si prega di verificarli");
				return;
			}

		}

		// Check if invoice needs to be updated
		String oldInvoiceID = null;
		Integer invoiceNumber = null;

		if (!invoiceNumberField.getText().isEmpty())
			invoiceNumber = Integer.parseInt(invoiceNumberField.getText());
		if (rspp.getInvoice() != null)
			oldInvoiceID = rspp.getInvoice().getId();
		newInvoice = new Invoice(invoiceNumber, invoiceEmissionDateField.getValue(), newAccount.getCategory(),
				payedCheck.isSelected());

		if (!newInvoice.equals(rspp.getInvoice()) && newInvoice.getId() != null) {
			if (isInvoiceChangeValid()) {
				model.updateInvoice(oldInvoiceID, newInvoice, rspp);
				isChanged = true;
			} else {
				warningWindows("Attenzione, i valori inseriti per la fattura non sono validi, si prega di verificarli");
				return;
			}

		}

		// Check if RSPP needs to be updated
		newRSPP = new RSPP(newJob, jobStartField.getValue(), jobEndField.getValue(), newInvoice);
		if (!newRSPP.equals(rspp) && newInvoice.getId() != null) {
			if (isRSPPChangeValid()) {
				model.updateRSPP(rspp.getJob().getId(), rspp.getStart(), newRSPP);
				isChanged = true;
			} else {
				warningWindows("Attenzione, i valori inseriti per l'RSPP non sono validi, si prega di verificarli");
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

	}
}