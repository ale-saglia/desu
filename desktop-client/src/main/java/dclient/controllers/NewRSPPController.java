package dclient.controllers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import dclient.model.Account;
import dclient.model.Job;
import dclient.model.JobPA;
import dclient.model.Model;
import dclient.model.RSPP;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
	Account selectedAccount;

	BiMap<String, Job> jobMap;

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

	public void initController(Model model) {
		this.model = model;

		rsppInfo.setDisable(true);

		refreshList();

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

	private void refreshList() {
		filteredAccountList = new FilteredList<Account>(FXCollections.observableArrayList(model.getAllAccounts()));
		accountListView.refresh();
	}

	public void selectAccount(Account keyAccount) {
		accountSearch.clear();
		refreshList();
		accountListView.getSelectionModel().select(keyAccount);
		enterAccount();
	}

	@FXML
	public void enterAccount() {
		Account selectedAccount = accountListView.getSelectionModel().getSelectedItem();
		if (selectedAccount != null) {
			rsppInfo.setDisable(false);
		}

		jobMap.clear();
		for (Job job : model.getAllJobOfAccount(selectedAccount))
			jobMap.put(job.getId(), job);

		jobCombo.getItems().clear();
		jobCombo.getItems().add("Nuovo...");
		jobCombo.getItems().addAll(jobMap.keySet());

		selectBestResult();
	}

	private void selectBestResult() {
		for (Job job : jobMap.values()) {
			if (job != null && job.getJobType().contains("RSPP"))
				jobCombo.getSelectionModel().select(jobMap.inverse().get(job));
		}
	}

	@FXML
	public void setFields() {
		if (jobMap.get(jobCombo.getSelectionModel().getSelectedItem()) != null) {
			Job job = jobMap.get(jobCombo.getSelectionModel().getSelectedItem());
			setJobFieldsBlocked(false);
			paHbox.setDisable(!(job instanceof JobPA));

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

		}
		else {
			setJobFieldsBlocked(true);
			paHbox.setDisable(false);
			jobNumber.clear();
			jobCategory.valueProperty().set(null);
			jobType.valueProperty().set(null);
			jobDescriptionField.clear();
			cigField.clear();
			decreeNumberField.clear();
			decreeDateField.valueProperty().set(null);
		}
	}

	private void setJobFieldsBlocked(boolean status) {
		jobNumber.setEditable(status);
		jobCategory.setEditable(status);
		jobType.setEditable(status);
		jobDescriptionField.setEditable(status);

		cigField.setEditable(status);
		decreeNumberField.setEditable(status);
		decreeDateField.setEditable(status);
	}
}