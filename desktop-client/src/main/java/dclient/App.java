package dclient;

import dclient.controllers.PasswordController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {
	@Override
	public void start(Stage stage) {

		try {
			FXMLLoader loader = new FXMLLoader(PasswordController.class.getResource("passwordPrompt.fxml"));
			HBox root = (HBox) loader.load();
			PasswordController passwordController = loader.getController();
			Scene scene = new Scene(root);
			passwordController.setWindow(stage, scene);
			
			scene.getStylesheets().add(PasswordController.class.getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.setTitle("Inserisci la password");
			stage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch();
	}

}