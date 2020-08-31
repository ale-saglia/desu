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
    
    @FXML
    void initialize() {
        assert nameField != null : "fx:id=\"nameField\" was not injected: check your FXML file 'newAccount.fxml'.";
        assert fiscalCodeText != null : "fx:id=\"fiscalCodeText\" was not injected: check your FXML file 'newAccount.fxml'.";
        assert numberVATField != null : "fx:id=\"numberVATField\" was not injected: check your FXML file 'newAccount.fxml'.";
        assert categoryAccountCombo != null : "fx:id=\"categoryAccountCombo\" was not injected: check your FXML file 'newAccount.fxml'.";
        assert atecoCodeField != null : "fx:id=\"atecoCodeField\" was not injected: check your FXML file 'newAccount.fxml'.";
        assert addressField != null : "fx:id=\"addressField\" was not injected: check your FXML file 'newAccount.fxml'.";
        assert closeButton != null : "fx:id=\"closeButton\" was not injected: check your FXML file 'newAccount.fxml'.";
        
        categoryAccountCombo.getItems().setAll(model.getAccountCategories().values());
    }

    void setParent(NewRSPPController parent) {
    	this.parent = parent;
    }
	
	void createNewAccount(){
		account = new Account(fiscalCodeText.getText(), nameField.getText(), numberVATField.getText(),
				atecoCodeField.getText(), addressField.getText(),
				model.getAccountCategories().inverse().get(categoryAccountCombo.getValue()));
		int result = model.newAccount(account);
		
		if(result >= 0) {
			parent.setAccount(account);
			closeButtonAction();
		}
		else {
			//Can't connect to DB or duplicate primary key
			Account existingAccount = model.getAccount(account.getFiscalCode());
			if(existingAccount != null) {
				//Duplicate Entry
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Valore duplicato");
				alert.setHeaderText("Attenzione il codice fiscale inserito corrisponde già al cliente" + existingAccount.getName());

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
