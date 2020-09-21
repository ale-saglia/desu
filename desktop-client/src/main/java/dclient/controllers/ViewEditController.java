package dclient.controllers;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.controlsfx.control.CheckComboBox;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewEditController {
	private final String DEFAULT_NEW_INVOICE_TEXT = "Nuova fattura...";

	private Model model;
	private RSPP rspp;

	MainController mainController;

	private BiMap<String, Invoice> invoiceMap;

	private Set<Integer> invoiceMonthsSet;
	static final ImmutableBiMap<String, Integer> MONTH_MAP = new ImmutableBiMap.Builder<String, Integer>()
			.put("Gennaio", 1).put("Febbraio", 2).put("Marzo", 3).put("Aprile", 4).put("Maggio", 5).put("Giugno", 6)
			.put("Luglio", 7).put("Agosto", 8).put("Settembre", 9).put("Ottobre", 10).put("Novembre", 11)
			.put("Dicembre", 12).build();

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
	private TextField descriptorField;

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
	private HBox invoiceComboParent;

	@FXML
	private ComboBox<String> invoiceBox;

	@FXML
	private TextField invoiceNumberField;

	@FXML
	private DatePicker invoiceEmissionDateField;

	@FXML
	private TextField invoiceTypeField;

	@FXML
	private CheckComboBox<String> invoiceMonthCombo;

	@FXML
	private TextField invoiceDescription;

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
		invoiceMap = rspp.getInvoiceMap();

		invoiceBox.getItems().clear();
		invoiceBox.getItems().add(DEFAULT_NEW_INVOICE_TEXT);
		invoiceBox.getItems().addAll(invoiceMap.keySet());
		if (rspp.getInvoices().size() > 0)
			invoiceBox.getSelectionModel().select(invoiceMap.entrySet().stream().findFirst().get().getKey());
		else
			invoiceComboParent.getChildren().clear();

		invoiceMonthCombo.getItems().setAll(MONTH_MAP.keySet());

		setAnagrafica();
		setJobs();
		setRSPP();

		setInvoiceMonth();
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
		descriptorField.setText(rspp.getJob().getCustomer().getDescriptor());
	}

	private void setJobs() {
		jobCodeField.setText(rspp.getJob().getId());
		categoryJobCombo.setValue(rspp.getJob().getJobCategory());
		categoryTypeCombo.setValue(rspp.getJob().getJobType());
		jobdDescriptionField.setText(rspp.getJob().getDescription());
		noteField.setText(model.getRSPPnote(rspp.getJob().getCustomer().getFiscalCode()));

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

	@FXML
	private void setInvoice() {
		Invoice invoice = invoiceMap.get(invoiceBox.getSelectionModel().getSelectedItem());
		if (invoice != null) {
			invoiceNumberField.setText(Integer.toString(invoice.getNumber()));
			invoiceEmissionDateField.setValue(invoice.getEmission());
			invoiceTypeField.setText(model.getAccountCategories().get(invoice.getType()));
			payedCheck.setSelected(invoice.getPayed());
			invoiceDescription.setText(invoice.getDescription());
		} else {
			invoiceNumberField.clear();
			invoiceEmissionDateField.setValue(null);
			invoiceTypeField.clear();
			payedCheck.setSelected(false);
			invoiceDescription.clear();
		}
	}

	private void setInvoiceMonth() {
		invoiceMonthsSet = model.getInvoiceMonths(rspp);
		if (invoiceMonthsSet == null)
			invoiceMonthsSet = new HashSet<Integer>();

		for (Integer month : invoiceMonthsSet) {
			invoiceMonthCombo.getCheckModel().check(MONTH_MAP.inverse().get(month));
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
		Set<Integer> newInvoiceMonths;
		Invoice newInvoice;
		String error;

		// Check if account needs to be update
		newAccount = new Account(fiscalCodeText.getText(), nameField.getText(), numberVATField.getText(),
				atecoCodeField.getText(), addressField.getText(),
				model.getAccountCategories().inverse().get(categoryAccountCombo.getValue()), descriptorField.getText());
		if (!newAccount.equals(rspp.getJob().getCustomer())) {
			error = FieldsValidator.isAccountValid(newAccount);
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
			error = FieldsValidator.isJobValid(newJob);
			if (error == null) {
				model.updateJob(rspp.getJob().getId(), newJob);
				isChanged = true;
			} else {
				warningWindows(error);
				return;
			}

		}

		// Check if rspp note needs to be updated
		if (noteField.getText() != null
				&& !noteField.getText().trim().equals(model.getRSPPnote(rspp.getJob().getCustomer().getFiscalCode()))) {
			error = FieldsValidator.isRSPPNoteValid(model.getRSPPnote(rspp.getJob().getCustomer().getFiscalCode()));
			if (error == null) {
				model.updateNote(fiscalCodeText.getText(), noteField.getText());
				isChanged = true;
			} else {
				warningWindows(error);
				return;
			}

		}

		// Check if RSPP needs to be updated
		newRSPP = new RSPP(newJob, jobStartField.getValue(), jobEndField.getValue());
		if (!newRSPP.equals(rspp)) {
			error = FieldsValidator.isRSPPChangeValid(newRSPP);
			if (error == null) {
				model.updateRSPP(rspp.getJob().getId(), rspp.getStart(), newRSPP);
				isChanged = true;
			} else {
				warningWindows(error);
				return;
			}

		}

		// Check if invoice months need to be updated
		newInvoiceMonths = new HashSet<Integer>();
		for (int i = 1; i <= 12; i++) {
			if (invoiceMonthCombo.getCheckModel().isChecked(MONTH_MAP.inverse().get(i)))
				newInvoiceMonths.add(i);
		}
		if (!newInvoiceMonths.equals(invoiceMonthsSet))
			model.updateInvoiceMonths(newRSPP, newInvoiceMonths);

		// Check if invoice needs to be updated
		newInvoice = new Invoice(invoiceNumberField.getText(), invoiceEmissionDateField.getValue(),
				newAccount.getCategory(), payedCheck.isSelected(), invoiceDescription.getText());

		System.out.println(invoiceMap.get(invoiceBox.getSelectionModel().getSelectedItem()));
		System.out.println(newInvoice.getId());

		//TODO fix bug when saving in empty invoice without changing
		if ((invoiceMap.get(invoiceBox.getSelectionModel().getSelectedItem()) == null && newInvoice.getId() != null)
				|| !(newInvoice.equals(invoiceMap.get(invoiceBox.getSelectionModel().getSelectedItem())))) {
			error = FieldsValidator.isInvoiceValid(newInvoice);
			if (error != null) {
				warningWindows(error);
				return;
			} else if ((error = FieldsValidator.isNewInvoiceDuplicate(model, newInvoice, newRSPP)) != null) {
				warningWindows(error);
				return;
			}

			if (invoiceMap.get(invoiceBox.getSelectionModel().getSelectedItem()) == null) {
				model.newInvoice(newInvoice);
				model.matchRSPPInvoice(newRSPP, newInvoice);
				isChanged = true;
			} else if (!newInvoice.equals(invoiceMap.get(invoiceBox.getSelectionModel().getSelectedItem()))) {
				model.updateInvoice(invoiceMap.get(invoiceBox.getSelectionModel().getSelectedItem()).getId(),
						newInvoice);
				isChanged = true;
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

		String invoiceTemp;
		if (invoiceMap.get(invoiceBox.getSelectionModel().getSelectedItem()) == null)
			invoiceTemp = DEFAULT_NEW_INVOICE_TEXT;
		else
			invoiceTemp = invoiceBox.getSelectionModel().getSelectedItem();
		setRSPP();
		invoiceBox.getSelectionModel().select(invoiceTemp);
		alert.showAndWait();
	}
}