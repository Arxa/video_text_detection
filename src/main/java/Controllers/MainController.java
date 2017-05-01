package Controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;

public class MainController
{
    /*
    GUI Elements
     */
    public Pane playInputVideo_Pane;
    public Button processVideo_Button;
    public ProgressBar progressBar1;
    public Button chooseVideoFile_Button;
    public TextArea extractedText_Area;
    public TextArea log_Area;
    public ProgressIndicator progressIndicator;
    public Label videoName;

    public MainController() {
    }

    public void loadAndPlayVideo(ActionEvent actionEvent) {
        Player.loadAndPlayVideo(playInputVideo_Pane, log_Area, processVideo_Button, chooseVideoFile_Button, videoName);
    }

    public void startProcessing(ActionEvent actionEvent)
    {
        chooseVideoFile_Button.setDisable(true);
        processVideo_Button.setDisable(true);
        VideoProcessor.processVideo(progressBar1, log_Area, chooseVideoFile_Button,
                progressIndicator, extractedText_Area);
    }
}


