
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Model;  

/**
 * JavaFX App
 */
public class App extends Application {
    @Override
    public void start(Stage stage) {
    	Model model;
    	
    	try {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
    		VBox root = (VBox) loader.load();
    		MainController controller = loader.getController();
    		model = new Model();
    		controller.setModel(model);
    		
    		stage.setMinWidth(640);
    		stage.setMinHeight(560);
    		
			Scene scene = new Scene(root, 960, 720);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
    		stage.setScene(scene);
    		stage.setTitle("Scadenziario RSPP");
    		stage.show();
    	}  catch (Exception e) {
			e.printStackTrace();
    	}
    	
    }

    public static void main(String[] args) {
        launch();
    }

}