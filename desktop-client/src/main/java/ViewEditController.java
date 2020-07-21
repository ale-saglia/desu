
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Model;
import model.RSPP;

public class ViewEditController {
	private Model model;
	private RSPP rspp;

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
    private ComboBox<String> categoryJobCombo;

    @FXML
    private ComboBox<String> categoryTypeCombo;

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
	
	public void setCombo() {
		categoryAccountCombo.getItems().setAll(model.getAccountCategories().values());
		categoryJobCombo.getItems().setAll(model.getJobCategories());
		categoryTypeCombo.getItems().setAll(model.getJobTypes());
	}

	public void setRSPP(String jobID, LocalDate jobStart) {
		rspp = model.getRSPP(jobID, jobStart);
		setAnagrafica();
	}

	public void setAnagrafica() {
		nameField.setText(rspp.getJob().getCustomer().getName());
		fiscalCodeText.setText(rspp.getJob().getCustomer().getFiscalCode());
		numberVATField.setText(rspp.getJob().getCustomer().getNumberVAT());
		categoryAccountCombo.setValue(model.getAccountCategories().get(rspp.getJob().getCustomer().getCategory()));
		atecoCodeField.setText(rspp.getJob().getCustomer().getAtecoCode());
		addressField.setText(rspp.getJob().getCustomer().getLegalAddress());
	}

	public void setJob() {

	}
}