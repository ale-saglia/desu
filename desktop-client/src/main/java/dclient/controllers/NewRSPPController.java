package dclient.controllers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import dclient.controllers.validator.FieldsValidator;
import dclient.model.Account;
import dclient.model.Job;
import dclient.model.JobPA;
import dclient.model.Model;
import dclient.model.RSPP;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NewRSPPController {
	Model model;
	RSPP rspp;

	MainController parent;

	BiMap<String, Job> jobMap;

	final String NEW_RSPP = "Nuovo...";

	ObservableList<Account> accountList;
	FilteredList<Account> filteredAccountList;

	@FXML
	private TextField accountSearch;

	@FXML
	private ListView<Account> accountListView;

	@FXML
	private VBox rsppInfo;

	@FXML
	private ComboBox<String> jobCombo;

	@FXML
	private TextField jobNumber;

	@FXML
	private ComboBox<String> jobCategory;

	@FXML
	private ComboBox<String> jobType;

	@FXML
	private TextField jobDescriptionField;

	@FXML
	private HBox paHbox;

	@FXML
	private TextField cigField;

	@FXML
	private TextField decreeNumberField;

	@FXML
	private DatePicker decreeDateField;

	@FXML
	private DatePicker rsppStart;

	@FXML
	private DatePicker rsppEnd;

	@FXML
	private TextArea rsppNotes;

	@FXML
	private Button closeButton;

	public void initController(MainController parent, Model model) {
		this.parent = parent;
		this.model = model;

		rsppInfo.setDisable(true);

		accountList = FXCollections.observableArrayList(model.getAllAccounts());
		filteredAccountList = new FilteredList<Account>(accountList);
		accountListView.setItems(filteredAccountList);

		paHbox.managedProperty().bind(paHbox.visibleProperty());

		System.out.println(filteredAccountList);

		accountListView.setItems(filteredAccountList);
		accountListView.setCellFactory(param -> new ListCell<Account>() {
			@Override
			protected void updateItem(Account item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null || item.getName() == null) {
					setText(null);
				} else {
					setText(item.getName());
				}
			}
		});

		filteredAccountList.predicateProperty().bind(javafx.beans.binding.Bindings.createObjectBinding(() -> {
			String text = accountSearch.getText();
			if (text == null || text.isEmpty()) {
				return null;
			} else {
				final String lowercase = text.toLowerCase();
				return (account) -> account.getName().toLowerCase().contains(lowercase);
			}
		}, accountSearch.textProperty()));

		jobMap = HashBiMap.create();

		jobCategory.getItems().setAll(model.getJobCategories());
		jobType.getItems().setAll(model.getJobTypes());

	}

	public void addNewAccount() {
		try {
			final Stage stage = new Stage();

			final FXMLLoader loader = new FXMLLoader(getClass().getResource("newAccount.fxml"));
			final VBox root = (VBox) loader.load();
			final NewAccountController controller = loader.getController();

			controller.setModel(model);
			controller.setCombo();
			controller.setParent(this);

			final Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.show();

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void refreshList() {
		accountList.setAll(model.getAllAccounts());
		accountListView.refresh();
	}

	public void selectAccount(Account keyAccount) {
		accountSearch.clear();
		refreshList();
		accountListView.getSelectionModel().clearAndSelect(accountListView.getItems().indexOf(keyAccount));
		accountListView.scrollTo(keyAccount);
		enterAccount();
	}

	@FXML
	public void enterAccount() {
		if (accountListView.getSelectionModel().getSelectedItem() != null) {
			rsppInfo.setDisable(false);
		}

		jobMap.clear();
		for (Job job : model.getAllJobOfAccount(accountListView.getSelectionModel().getSelectedItem()))
			jobMap.put(job.getId(), job);

		jobCombo.getItems().clear();
		jobCombo.getItems().add(NEW_RSPP);
		jobCombo.getItems().addAll(jobMap.keySet());

		selectBestResult();
	}

	private void selectBestResult() {
		for (Job job : jobMap.values()) {
			if (job != null && job.getJobType().contains("RSPP"))
				jobCombo.getSelectionModel().select(jobMap.inverse().get(job));
		}
		if (jobCombo.getSelectionModel().getSelectedItem() == null)
			jobCombo.getSelectionModel().select(NEW_RSPP);
	}

	@FXML
	public void setFields() {
		clearFields();
		if (jobMap.get(jobCombo.getSelectionModel().getSelectedItem()) != null) {
			Job job = jobMap.get(jobCombo.getSelectionModel().getSelectedItem());
			setJobFieldsEditable(false);
			paHbox.setVisible(job instanceof JobPA);

			jobNumber.setText(job.getId());
			jobCategory.getItems().setAll(model.getJobCategories());
			jobCategory.getSelectionModel().select(job.getJobCategory());
			jobType.getItems().setAll(model.getJobTypes());
			jobType.getSelectionModel().select(job.getJobType());

			jobDescriptionField.setText(job.getDescription());

			rsppNotes.setText(model.getRSPPnote(job.getCustomer().getFiscalCode()));

			if (job instanceof JobPA) {
				JobPA jobPA = (JobPA) job;
				cigField.setText(jobPA.getCig());
				decreeNumberField.setText(Integer.toString(jobPA.getDecreeNumber()));
				decreeDateField.setValue(jobPA.getDecreeDate());
			}

		} else {
			if (accountListView.getSelectionModel().getSelectedItem().getCategory().contains("pa"))
				paHbox.setVisible(true);
			else
				paHbox.setVisible(false);
			setJobFieldsEditable(true);
		}
	}

	private void clearFields() {
		jobNumber.clear();
		jobCategory.valueProperty().set(null);
		jobType.valueProperty().set(null);
		jobDescriptionField.clear();
		cigField.clear();
		decreeNumberField.clear();
		decreeDateField.valueProperty().set(null);
	}

	private void setJobFieldsEditable(boolean status) {
		jobNumber.setEditable(status);
		jobCategory.setEditable(status);
		jobType.setEditable(status);
		jobDescriptionField.setEditable(status);

		cigField.setEditable(status);
		decreeNumberField.setEditable(status);
		decreeDateField.setEditable(status);
	}

	@FXML
	public void addRSPP() {
		Job job = jobMap.get(jobCombo.getSelectionModel().getSelectedItem());

		if (job == null) {
			job = new Job(jobNumber.getText(), jobCategory.getValue(), jobType.getValue(),
					jobDescriptionField.getText(), accountListView.getSelectionModel().getSelectedItem());
			String error = FieldsValidator.isJobChangeValid(job);
			if (error == null) {
				if (!accountListView.getSelectionModel().getSelectedItem().getCategory().contains("pa"))
					model.newJob(job);
				else {
					JobPA jobPA = new JobPA(job, cigField.getText(), Integer.valueOf(decreeNumberField.getText()),
							decreeDateField.getValue());
					error = FieldsValidator.isJobPAValid(jobPA);
					if (error != null)
						model.newJob(jobPA);
					else
						warningWindows(error);
				}

				enterAccount();
			} else
				warningWindows(error);
		}

		RSPP rspp = new RSPP(job, rsppStart.getValue(), rsppEnd.getValue(), null);
		String error = FieldsValidator.isRSPPChangeValid(rspp);
		if (error == null) {
			model.newRSPP(rspp);
		} else
			warningWindows(error);
		parent.refresh();
	}

	@FXML
	public void saveAndClose() {
		addRSPP();
		closeButtonAction();
	}

	private void warningWindows(String message) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Attenzione");
		alert.setHeaderText("Sono stati rilevati i seguenti campi non validi:");
		alert.setContentText(message);

		alert.showAndWait();
	}

	@FXML
	private void closeButtonAction() {
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void duplicateLastRSPP() {
		// TODO
	}
}