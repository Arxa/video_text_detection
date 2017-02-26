package Controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class MainController
{
    // View Elements
    public Pane pane;
    public Pane pane2;
    public Pane pane3;
    public Pane pane4;
    public Button process;
    public Label label1;

    public MainController() {
    }

    public void loadAndPlayVideo(ActionEvent actionEvent) {
        Player.loadAndPlayVideo(label1, pane);
    }

    public void startProcessing(ActionEvent actionEvent) {
        VideoProcessor.processVideo(pane2,label1);
    }
}


