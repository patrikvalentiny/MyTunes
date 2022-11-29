package mytunes.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mytunes.gui.models.Model;

public class NewPlaylistViewController {
    @FXML
    private TextField nameTextField;
    private Model model = new Model();

    public void saveButtonAction(ActionEvent actionEvent) {
        //TODO create a new playlist and save it to database
    }

    public void cancelButtonAction(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        node.getScene().getWindow().hide();
    }
}
