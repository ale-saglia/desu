package dclient.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.controlsfx.control.CheckComboBox;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import dclient.controllers.validator.FieldsValidator;
import dclient.db.dao.AccountDAO;
import dclient.db.dao.InvoiceDAO;
import dclient.db.dao.JobDAO;
import dclient.db.dao.RsppDAO;
import dclient.model.Account;
import dclient.model.Invoice;
import dclient.model.Job;
import dclient.model.JobPA;
import dclient.model.Model;
import dclient.model.Rspp;
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
import javafx.util.StringConverter;

public class ViewEditController {
	private final String DEFAULT_NEW_INVOICE_TEXT = "Nuova fattura...";

	private Model model;
	private Rspp rspp;
	private String rsppNote;

	MainController mainController;

	private BiMap<String, Invoice> invoiceMap;

	private Collection<Integer> invoiceMonths;
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
	private ComboBox<String> jobCategory;

	@FXML
	private ComboBox<String> jobType;

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
	void initialize() {
		assert nameField != null : "fx:id=\"nameField\" was not injected: check your FXML file 'edit.fxml'.";
		assert fiscalCodeText != null : "fx:id=\"fiscalCodeText\" was not injected: check your FXML file 'edit.fxml'.";
		assert numberVATField != null : "fx:id=\"numberVATField\" was not injected: check your FXML file 'edit.fxml'.";
		assert categoryAccountCombo != null
				: "fx:id=\"categoryAccountCombo\" was not injected: check your FXML file 'edit.fxml'.";
		assert atecoCodeField != null : "fx:id=\"atecoCodeField\" was not injected: check your FXML file 'edit.fxml'.";
		assert addressField != null : "fx:id=\"addressField\" was not injected: check your FXML file 'edit.fxml'.";
		assert descriptorField != null
				: "fx:id=\"descriptorField\" was not injected: check your FXML file 'edit.fxml'.";
		assert jobCodeField != null : "fx:id=\"jobCodeField\" was not injected: check your FXML file 'edit.fxml'.";
		assert jobCategory != null : "fx:id=\"jobCategory\" was not injected: check your FXML file 'edit.fxml'.";
		assert jobType != null : "fx:id=\"jobType\" was not injected: check your FXML file 'edit.fxml'.";
		assert jobdDescriptionField != null
				: "fx:id=\"jobdDescriptionField\" was not injected: check your FXML file 'edit.fxml'.";
		assert paReferences != null : "fx:id=\"paReferences\" was not injected: check your FXML file 'edit.fxml'.";
		assert cigField != null : "fx:id=\"cigField\" was not injected: check your FXML file 'edit.fxml'.";
		assert decreeNumberField != null
				: "fx:id=\"decreeNumberField\" was not injected: check your FXML file 'edit.fxml'.";
		assert decreeDateField != null
				: "fx:id=\"decreeDateField\" was not injected: check your FXML file 'edit.fxml'.";
		assert noteField != null : "fx:id=\"noteField\" was not injected: check your FXML file 'edit.fxml'.";
		assert jobStartField != null : "fx:id=\"jobStartField\" was not injected: check your FXML file 'edit.fxml'.";
		assert jobEndField != null : "fx:id=\"jobEndField\" was not injected: check your FXML file 'edit.fxml'.";
		assert invoiceComboParent != null
				: "fx:id=\"invoiceComboParent\" was not injected: check your FXML file 'edit.fxml'.";
		assert invoiceBox != null : "fx:id=\"invoiceBox\" was not injected: check your FXML file 'edit.fxml'.";
		assert invoiceNumberField != null
				: "fx:id=\"invoiceNumberField\" was not injected: check your FXML file 'edit.fxml'.";
		assert invoiceEmissionDateField != null
				: "fx:id=\"invoiceEmissionDateField\" was not injected: check your FXML file 'edit.fxml'.";
		assert invoiceTypeField != null
				: "fx:id=\"invoiceTypeField\" was not injected: check your FXML file 'edit.fxml'.";
		assert invoiceMonthCombo != null
				: "fx:id=\"invoiceMonthCombo\" was not injected: check your FXML file 'edit.fxml'.";
		assert invoiceDescription != null
				: "fx:id=\"invoiceDescription\" was not injected: check your FXML file 'edit.fxml'.";
		assert payedCheck != null : "fx:id=\"payedCheck\" was not injected: check your FXML file 'edit.fxml'.";
		assert closeButton != null : "fx:id=\"closeButton\" was not injected: check your FXML file 'edit.fxml'.";
		
		decreeDateField.setConverter(new StringConverter<LocalDate>() {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(model.getLocalDateFormat());

			@Override
			public String toString(LocalDate date) {
				if (date != null) {
					return dateFormatter.format(date);
				} else {
					return "";
				}
			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, dateFormatter);
				} else {
					return null;
				}
			}
		});

		jobStartField.setConverter(new StringConverter<LocalDate>() {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(model.getLocalDateFormat());

			@Override
			public String toString(LocalDate date) {
				if (date != null) {
					return dateFormatter.format(date);
				} else {
					return "";
				}
			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, dateFormatter);
				} else {
					return null;
				}
			}
		});

		jobEndField.setConverter(new StringConverter<LocalDate>() {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(model.getLocalDateFormat());

			@Override
			public String toString(LocalDate date) {
				if (date != null) {
					return dateFormatter.format(date);
				} else {
					return "";
				}
			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, dateFormatter);
				} else {
					return null;
				}
			}
		});
	}

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
		jobCategory.getItems().setAll(model.getJobCat().keySet());
	}

	@FXML
	public void updateJobCombo() {
		jobType.getItems().clear();
		jobType.getItems().addAll((model.getJobCat().get(jobCategory.getSelectionModel().getSelectedItem())));
	}

	public void setRSPP(String jobID, LocalDate jobStart) {
		rspp = RsppDAO.getRSPP(model.getConMan().getDBConnection(), jobID, jobStart);
		rsppNote = RsppDAO.getRSPPnote(model.getConMan().getDBConnection(), rspp.getJob().getCustomer());
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

		model.getConMan().closeDBConnection();
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
		jobCategory.setValue(rspp.getJob().getJobCategory());
		updateJobCombo();
		jobType.setValue(rspp.getJob().getJobType());
		jobdDescriptionField.setText(rspp.getJob().getDescription());
		noteField.setText(rsppNote);

		if (rspp.getJob() instanceof JobPA) {
			System.out.println("Ciao");
			cigField.setText(((JobPA) rspp.getJob()).getCig());
			decreeNumberField.setText(((JobPA) rspp.getJob()).getDecreeNumberString());
			decreeDateField.setValue(((JobPA) rspp.getJob()).getDecreeDate());
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
		invoiceMonths = RsppDAO.getInvoiceMonths(model.getConMan().getDBConnection(), rspp.getJob().getCustomer());
		if (invoiceMonths == null)
			invoiceMonths = new HashSet<Integer>();

		for (Integer month : invoiceMonths) {
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
		Rspp newRSPP;
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
				AccountDAO.updateAccount(model.getConMan().getDBConnection(),
						rspp.getJob().getCustomer().getFiscalCode(), newAccount);
				isChanged = true;
			} else {
				warningWindows(error);
				return;
			}

		}

		// Check if job needs to be updated
		newJob = new Job(jobCodeField.getText(), jobCategory.getValue(), jobType.getValue(),
				jobdDescriptionField.getText(), newAccount);
		if (rspp.getJob() instanceof JobPA) {
			newJob = new JobPA(newJob, cigField.getText(), decreeNumberField.getText(), decreeDateField.getValue());
		}
		if (!newJob.equals(rspp.getJob())) {
			error = FieldsValidator.isJobValid(newJob);
			if (error == null) {
				// TODO check if right
				JobDAO.updateJob(model.getConMan().getDBConnection(), newJob, rspp.getJob().getId());
				isChanged = true;
			} else {
				warningWindows(error);
				return;
			}

		}

		// Check if rspp note needs to be updated
		if (noteField.getText() != null && !noteField.getText().trim().equals(rsppNote)) {
			error = FieldsValidator.isRSPPNoteValid(rsppNote);
			if (error == null) {
				RsppDAO.updateNote(model.getConMan().getDBConnection(), newAccount, noteField.getText());
				isChanged = true;
			} else {
				warningWindows(error);
				return;
			}

		}

		// Check if RSPP needs to be updated
		newRSPP = new Rspp(newJob, jobStartField.getValue(), jobEndField.getValue());
		if (!newRSPP.equals(rspp)) {
			error = FieldsValidator.isRSPPChangeValid(newRSPP);
			if (error == null) {
				RsppDAO.updateRSPP(model.getConMan().getDBConnection(), rspp.getJob().getId(), rspp.getStart(),
						newRSPP);
				isChanged = true;
			} else {
				warningWindows(error);
				return;
			}

		}

		// Check if invoice months need to be updated
		newInvoiceMonths = new HashSet<Integer>();
		for (int i = 1; i <= MONTH_MAP.size(); i++) {
			if (invoiceMonthCombo.getCheckModel().isChecked(MONTH_MAP.inverse().get(i)))
				newInvoiceMonths.add(i);
		}
		if (!newInvoiceMonths.equals(invoiceMonths))
			RsppDAO.updateInvoiceMonths(model.getConMan().getDBConnection(), newRSPP, newInvoiceMonths);

		// Check if invoice needs to be updated
		newInvoice = new Invoice(invoiceNumberField.getText(), invoiceEmissionDateField.getValue(),
				newAccount.getCategory(), payedCheck.isSelected(), invoiceDescription.getText());

		if (!(newInvoice.equals(invoiceMap.get(invoiceBox.getSelectionModel().getSelectedItem())))
				&& !((invoiceMap.get(invoiceBox.getSelectionModel().getSelectedItem()) == null
						&& newInvoice.getId() == null))) {
			error = FieldsValidator.isInvoiceValid(newInvoice);
			if (error != null) {
				warningWindows(error);
				return;
			} else if ((error = FieldsValidator.isNewInvoiceDuplicate(model, newInvoice, newRSPP)) != null) {
				warningWindows(error);
				return;
			}

			if (invoiceMap.get(invoiceBox.getSelectionModel().getSelectedItem()) == null) {
				InvoiceDAO.newInvoice(model.getConMan().getDBConnection(), newInvoice);
				RsppDAO.matchRSPPInvoice(model.getConMan().getDBConnection(), newRSPP, newInvoice);
				isChanged = true;
			} else if (!newInvoice.equals(invoiceMap.get(invoiceBox.getSelectionModel().getSelectedItem()))) {
				InvoiceDAO.updateInvoice(model.getConMan().getDBConnection(),
						invoiceMap.get(invoiceBox.getSelectionModel().getSelectedItem()).getId(), newInvoice);
				isChanged = true;
			}

		}

		if (isChanged) {
			System.out.println("Some elements were modified");
			mainController.refresh();
		}
		model.getConMan().closeDBConnection();
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