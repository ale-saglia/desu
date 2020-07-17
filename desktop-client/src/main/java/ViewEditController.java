
import java.io.IOException;
import java.time.LocalDate;

import db.SicurteaDAO;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Account;
import model.Job;
import model.RSPP;

public class ViewEditController {
	SicurteaDAO dao = new SicurteaDAO();

	RSPP rspp;
	Job job;
	Account account;
	
	@FXML
    private TextField nameField;

    @FXML
    private TextField fiscalCodeText;

    @FXML
    private TextField numberVATField;

    @FXML
    private TextField categoryField;

    @FXML
    private TextField atecoCodeField;

    @FXML
    private TextField addressField;

	@FXML
	private javafx.scene.control.Button closeButton;

	@FXML
	private void closeButtonAction() {
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
	}

	public void setRSPP(String jobID, LocalDate jobStart, LocalDate jobEnd) {
		job = dao.getJob(jobID);
		System.out.println(job);
		account = dao.getAccountFromJob(job.getId());
		System.out.println(account);
		setAnagrafica();
	}
	
	public void setAnagrafica() {
		nameField.setText(account.getName());
		fiscalCodeText.setText(account.getFiscalCode());
		numberVATField.setText(account.getNumberVAT());
		categoryField.setText(account.getCategory());
		atecoCodeField.setText(account.getAtecoCode());
		addressField.setText(account.getLegalAddress());		
	}
}