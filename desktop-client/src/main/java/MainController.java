

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import model.Model;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class MainController {
	public void switchToViewEdit() throws Exception {           
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("edit.fxml"));
            Parent rootViewEdit = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(rootViewEdit));  
            stage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }
	public void setModel(Model model) {
	}
	
	private class RSPPtable extends MainController{
		@FXML private TableView<RSPPtableElement> rsppTable;
	    
	    public RSPPtable(Model model) {
	    	rsppTable = new TableView<RSPPtableElement>();
	    	
	    	TableColumn<RSPPtableElement, String> nameColumn = new TableColumn<RSPPtableElement, String>("Ragione Sociale");
	    	nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
	    	
	    }
	    
	    private class RSPPtableElement{
	    	String jobID;
	    	Date jobStart;
	    	
	    	Date jobEnd;
	    	String accountName;
	    	String category;
	    	String invoiceID;
	    	boolean payed;
	    	
	    	
			public RSPPtableElement(Map<String, String> rsspElement) {
				this.jobID = rsspElement.get("jobid");
				try {
					this.jobStart = new SimpleDateFormat("dd/MM/yyyy").parse(rsspElement.get("jobstart"));
					this.jobEnd = new SimpleDateFormat("dd/MM/yyyy").parse(rsspElement.get("jobend"));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				this.accountName = rsspElement.get("name");
				switch(rsspElement.get("category")) {
				  case "b2b":
					  this.category = "Azienda";
				    break;
				  case "b2c":
					  this.category = "Privato";
				    break;
				  case "pa":
					  this.category = "Pubblica Amministrazione";
					    break;
				  default:
				    throw new IllegalArgumentException();
				}

				this.invoiceID = rsspElement.get("invoiceid");
				
				if (rsspElement.get("category") == "true")
					payed = true;
				else
					payed = false;
			}


			public String getJobID() {
				return jobID;
			}


			public Date getJobStart() {
				return jobStart;
			}


			public Date getJobEnd() {
				return jobEnd;
			}


			public String getAccountName() {
				return accountName;
			}


			public String getCategory() {
				return category;
			}


			public String getInvoiceID() {
				return invoiceID;
			}


			public boolean isPayed() {
				return payed;
			}
	    }
	}
}
