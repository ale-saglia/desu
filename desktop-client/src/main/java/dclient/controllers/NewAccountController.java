package dclient.controllers;

import dclient.model.Account;
import dclient.model.Model;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewAccountController {
	NewRSPPController parent;
	Model model;
	Account account;

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
	private Button closeButton;

	public void setModel(Model model) {
		this.model = model;
	}

	public void setCombo() {
		categoryAccountCombo.getItems().setAll(model.getAccountCategories().values());

	}

	void setParent(NewRSPPController parent) {
		this.parent = parent;
	}

	@FXML
	void createNewAccount() throws InterruptedException {
		account = new Account(fiscalCodeText.getText(), nameField.getText(), numberVATField.getText(),
				atecoCodeField.getText(), addressField.getText(),
				model.getAccountCategories().inverse().get(categoryAccountCombo.getValue()));
		int result = model.newAccount(account);

		//TODO new account doesn't show up in list
		if (result >= 0) {
			parent.refreshList();
			parent.selectAccount(account);
			closeButtonAction();
		} else {
			// Can't connect to DB or duplicate primary key
			Account existingAccount = model.getAccount(account.getFiscalCode());
			if (existingAccount != null) {
				// Duplicate Entry
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Valore duplicato");
				alert.setHeaderText(
						"Attenzione il codice fiscale inserito corrisponde già al cliente " + existingAccount.getName());

				alert.showAndWait();
			}

		}
	}

	@FXML
	private void closeButtonAction() {
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
	}
}
