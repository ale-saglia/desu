

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
	    	
	    }
	}
}
