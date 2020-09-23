package dclient.controllers;

import java.time.Period;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import dclient.DClient;
import dclient.controllers.validator.FieldsValidator;
import dclient.db.dao.AccountDAO;
import dclient.db.dao.JobDAO;
import dclient.db.dao.RsppDAO;
import dclient.model.Account;
import dclient.model.Job;
import dclient.model.JobPA;
import dclient.model.Model;
import dclient.model.Rspp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
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
	Rspp rspp;

	boolean safeExit;

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

		accountList = FXCollections.observableArrayList(AccountDAO.getAccounts(model.getConMan().getDBConnection()));
		filteredAccountList = new FilteredList<Account>(accountList, s -> true);
		accountListView.setItems(filteredAccountList);

		paHbox.managedProperty().bind(paHbox.visibleProperty());

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
			// TODO search in both account name and descriptor
			String text = accountSearch.getText();
			if (text == null || text.isEmpty()) {
				return null;
			} else {
				return ((account) -> account.getName().toLowerCase().contains(text.toLowerCase()));
			}
		}, accountSearch.textProperty()));

		jobMap = HashBiMap.create();

		jobCategory.getItems().setAll(model.getJobCat().keySet());
		updateJobCombo();

		model.getConMan().closeDBConnection();
	}

	@FXML
	public void updateJobCombo() {
		if (jobCategory.getSelectionModel().getSelectedItem() == null || model.getJobCat().get(jobCategory.getSelectionModel().getSelectedItem()) == null)
			jobType.setDisable(true);
		else {
			jobType.setDisable(false);
			jobType.getItems().setAll(model.getJobCat().get(jobCategory.getSelectionModel().getSelectedItem()));
		}
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
			stage.setTitle("Crea campo anagrafica");
			stage.getIcons().add(new Image(DClient.class.getResourceAsStream("logo.png")));
			stage.show();

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void refreshList() {
		accountList.setAll(AccountDAO.getAccounts(model.getConMan().getDBConnection()));
		accountListView.refresh();
		model.getConMan().closeDBConnection();
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
		safeExit = true;
		if (accountListView.getSelectionModel().getSelectedItem() != null) {
			rsppInfo.setDisable(false);
		}

		jobMap.clear();
		for (Job job : JobDAO.getJobs(model.getConMan().getDBConnection(),
				accountListView.getSelectionModel().getSelectedItem()))
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
			jobCategory.getItems().setAll(model.getJobCat().keySet());
			jobCategory.getSelectionModel().select(job.getJobCategory());
			updateJobCombo();
			jobType.getSelectionModel().select(job.getJobType());

			jobDescriptionField.setText(job.getDescription());

			rsppNotes.setText(RsppDAO.getRSPPnote(model.getConMan().getDBConnection(), job.getCustomer()));

			if (job instanceof JobPA) {
				JobPA jobPA = (JobPA) job;
				cigField.setText(jobPA.getCig());
				decreeNumberField.setText(Integer.toString(jobPA.getDecreeNumber()));
				decreeDateField.setValue(jobPA.getDecreeDate());
			}

			Rspp lastRSPP = RsppDAO.getLastRSPP(model.getConMan().getDBConnection(),
					accountListView.getSelectionModel().getSelectedItem());
			if (lastRSPP != null) {
				rsppStart.setValue(lastRSPP.getEnd().plusDays(1));
				rsppEnd.setValue(rsppStart.getValue()
						.plus(Period.between(lastRSPP.getStart(), lastRSPP.getEnd().plusDays(1))).minusDays(1));
			} else {
				if (accountListView.getSelectionModel().getSelectedItem().getCategory().contains("pa"))
					paHbox.setVisible(true);
				else
					paHbox.setVisible(false);
				setJobFieldsEditable(true);
			}
		} else {
			setJobFieldsEditable(true);
			paHbox.setVisible(accountListView.getSelectionModel().getSelectedItem().getCategory().contains("pa"));
		}
		model.getConMan().closeDBConnection();
	}

	private void clearFields() {
		jobNumber.clear();
		jobCategory.valueProperty().set(null);
		jobType.valueProperty().set(null);
		jobDescriptionField.clear();
		cigField.clear();
		decreeNumberField.setText("");
		decreeDateField.valueProperty().set(null);

		rsppStart.valueProperty().set(null);
		rsppEnd.valueProperty().set(null);
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

		// Add JobPA details if job exist but missing it
		if ((!cigField.getText().isEmpty() || !decreeNumberField.getText().isEmpty()
				|| decreeDateField.getValue() != null) && job instanceof JobPA) {
			JobPA jobPA = new JobPA(job, cigField.getText(), Integer.valueOf(decreeNumberField.getText()),
					decreeDateField.getValue());
			String error = FieldsValidator.isJobValid(jobPA);

			if (error == null)
				JobDAO.addJobPAInfos(model.getConMan().getDBConnection(), jobPA);
			else {
				warningWindows(error);
				return;
			}

		}

		// Add new Job if it is missing
		if (job == null) {
			job = new Job(jobNumber.getText(), jobCategory.getValue(), jobType.getValue(),
					jobDescriptionField.getText(), accountListView.getSelectionModel().getSelectedItem());

			String error = FieldsValidator.isJobValid(job);
			if (error == null) {
				if (!accountListView.getSelectionModel().getSelectedItem().getCategory().contains("pa"))
					JobDAO.newJob(model.getConMan().getDBConnection(), job);
				else {
					job = new JobPA(job, cigField.getText(), decreeNumberField.getText(), decreeDateField.getValue());
					error = FieldsValidator.isJobValid(job);
					if (error == null)
						JobDAO.newJob(model.getConMan().getDBConnection(), job);
					else {
						warningWindows(error);
						return;
					}

				}

				enterAccount();
			} else {
				warningWindows(error);
				return;
			}

		}

		Rspp rspp = new Rspp(job, rsppStart.getValue(), rsppEnd.getValue());
		String error = FieldsValidator.isRSPPChangeValid(rspp);
		if (error == null) {
			RsppDAO.newRSPP(model.getConMan().getDBConnection(), rspp);
		} else
			warningWindows(error);
		enterAccount();

		parent.refresh();
	}

	@FXML
	public void saveAndClose() {
		addRSPP();
		if (safeExit)
			closeButtonAction();
	}

	private void warningWindows(String message) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Attenzione");
		alert.setHeaderText("Sono stati rilevati i seguenti campi non validi:");
		alert.setContentText(message);

		safeExit = false;

		alert.showAndWait();
	}

	@FXML
	private void closeButtonAction() {
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
	}
}