package dclient.controllers;

import dclient.model.Account;
import dclient.model.Job;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class NewRSPPController {
	Model model;
	RSPP rspp;
	Account selectedAccount;

	FilteredList<Account> filteredAccountList;

	@FXML
	private TextField accountSearch;

	@FXML
	private ListView<Account> accountListView;

	@FXML
	private VBox rsppInfo;

	@FXML
	private ComboBox<Job> jobCombo;

	@FXML
	private TextField jobNumber;

	@FXML
	private ComboBox<String> jobCategory;

	@FXML
	private ComboBox<String> jobType;

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

		jobCombo.getItems().setAll(model.getAllJobOfAccount(selectedAccount));
		jobCombo.setCellFactory(param -> new ListCell<Job>() {
			@Override
			protected void updateItem(Job item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null || item.getId() == null) {
					setText(null);
				} else {
					setText(item.getId());
				}
			}
		});

		for (Job job : jobCombo.getItems()) {
			if (job.getJobType().equals("RSPP") && (jobCombo.getSelectionModel().getSelectedItem() == null
					|| job.getId().compareTo(jobCombo.getSelectionModel().getSelectedItem().getId()) > 0)) {
				jobCombo.getSelectionModel().select(job);
			}
		}
	}
}