package com.sicurtea.gest.dclient;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;

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
}
