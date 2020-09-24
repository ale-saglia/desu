package dclient.controllers;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dclient.DClient;
import dclient.model.Model;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class PasswordController {
	private static Logger logger = LoggerFactory.getLogger(DClient.class);

	@FXML
	private PasswordField passwordField;

	Model model;

	Stage stage;
	Scene scene;

	private String password;

	@FXML
	void initialize() {
		assert passwordField != null : "fx:id=\"passwordField\" was not injected: check your FXML file 'passwordPrompt.fxml'.";
	}

	public void setWindow(Stage stage, Scene scene) {
		this.stage = stage;
		this.scene = scene;
	}

	public void setPassword() {
		password = passwordField.getText();
		launchMainView();
		stage = (Stage) passwordField.getScene().getWindow();
	    stage.close();
	}

	private void launchMainView() {
		FXMLLoader loader = new FXMLLoader(MainController.class.getResource("main.fxml"));
		try {
			VBox root = (VBox) loader.load();
			stage = new Stage();

			MainController controller = loader.getController();
			model = new Model(password);
			controller.setModel(model);
			controller.createTable();

			stage.setMinWidth(640);
			stage.setMinHeight(560);

			scene = new Scene(root, 960, 720);

			scene.getStylesheets().add(MainController.class.getResource("application.css").toExternalForm());
			stage.setScene(scene);
			stage.setTitle("Scadenziario RSPP");
			stage.getIcons().add(new Image(DClient.class.getResourceAsStream("logo.png")));
			stage.show();
			stage.setOnCloseRequest((EventHandler<WindowEvent>) we -> {
				logger.info("\n" + model.closeSession());

				Platform.exit();
				System.exit(0);
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}