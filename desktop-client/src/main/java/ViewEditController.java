

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class ViewEditController {
    //TODO Implement constructor to identify sent field

    @FXML private javafx.scene.control.Button closeButton;

    @FXML
    private void switchToPrimary() throws IOException {
        //App.setRoot("main");
    }

    @FXML
    private void closeButtonAction(){
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}