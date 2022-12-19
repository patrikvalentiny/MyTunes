package mytunes.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import mytunes.be.Playlist;
import mytunes.gui.models.Model;

import java.sql.SQLException;

public class NewPlaylistViewController {
    @FXML
    private TextField nameTextField;
    private Model model = null;
    private boolean isEditing = false;

    public void setModel(Model model) {
        this.model = model;
    }

    @FXML
    public void initialize(){
        isEditing = false;
    }

    @FXML
    private void btnSaveAction(ActionEvent actionEvent) {
        String name = nameTextField.getText().trim();
        if (!name.isEmpty())
            nameTextField.promptTextProperty().setValue("");
        if (!name.isEmpty() && !nameTextField.promptTextProperty().getValue().equals("Field must not be empty!")
                && !nameTextField.promptTextProperty().getValue().equals("Enter the name of the playlist")){
            nameTextField.promptTextProperty().setValue("");
            try {
                if (isEditing)
                    model.updatePlaylist(new Playlist(name));
                else
                    model.createPlaylist(new Playlist(name));
                Node node = (Node) actionEvent.getSource();
                node.getScene().getWindow().hide();
            } catch (SQLException e) {
                // if playlist name already exists
                if (e.getMessage().contains("UQ__ALL_PLAY__DFCC3A56AFE65860")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("This playlist already exists");
                    alert.showAndWait();
                    nameTextField.setText("");
                    nameTextField.promptTextProperty().setValue("Make original playlist name!");
                } else {
                    e.printStackTrace();
                }
            }
        }
        else
            nameTextField.promptTextProperty().setValue("Field must not be empty!");
    }

    @FXML
    private void cancelButtonAction(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        node.getScene().getWindow().hide();
    }

    public void setIsEditing(){
        isEditing = true;
        Playlist playlistToEdit = model.getPlaylistToEdit();
        nameTextField.setText(playlistToEdit.getName());
    }
}
