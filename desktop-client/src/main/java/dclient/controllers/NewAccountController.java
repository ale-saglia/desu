package dclient.controllers;

import dclient.controllers.validator.FieldsValidator;
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
    private TextField descriptorField;
	
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
				model.getAccountCategories().inverse().get(categoryAccountCombo.getValue()), descriptorField.getText());

		String error = FieldsValidator.isAccountValid(account);
		if (error != null) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("ATTENZIONE: campi non validi");
			alert.setHeaderText("Attenzione sono stati rilevati campi non validi");
			alert.setContentText(error);
			alert.showAndWait();
			return;
		}

		error = FieldsValidator.isNewAccountDuplicate(model, account);
		if (error != null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("ATTENZIONE: campi duplicati");
			alert.setHeaderText(
					"Attenzione sono stati rilevati conflitti nel database e non è possibile inserire i dati");
			alert.setContentText(error);
			alert.showAndWait();
			return;
		}

		int result = model.newAccount(account);

		if (result >= 0) {
			parent.refreshList();
			parent.selectAccount(account);
			closeButtonAction();
		}
	}

	@FXML
	private void closeButtonAction() {
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
	}
}
