package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;

public class MainController
{
    /*
    UI Elements
     */

    @FXML
    private Pane playInputVideo_Pane;
    @FXML
    private Button processVideo_Button;
    @FXML
    private ProgressBar progressBar1;
    @FXML
    private Button chooseVideoFile_Button;
    @FXML
    private TextArea extractedText_Area;
    @FXML
    private TextArea log_Area;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label videoName;
    @FXML
    private Label testFX;

    private static File filename;

    /*
    UI Events
     */

    @FXML
    private void chooseVideoFile(MouseEvent actionEvent)
    {
        filename = Player.getFilenameFromDialog(this);
        validateVideoFile(filename);
    }

    @FXML
    private void startProcessing(ActionEvent actionEvent)
    {
        disableChooseVideoFileButton();
        disableProcessVideoButton();
        VideoProcessor.processVideo(this);
    }

    public void validateVideoFile(File filename)
    {
        int status = Player.validateFileName(filename, this);
        if (status == 1)
        {
            try
            {
                playVideoToPane(filename);
                appendToLog("Video file loaded successfully");
                changeVideoNameLabel(filename.getName());
                enableProcessVideoButton();
                Player.updateDirectories(this);
            }
            catch (Exception e) {
                appendToLog("JAVA-FX VIDEO PLAYER ERROR");
            }
        }
    }


    /*
    UI Binding Methods
     */

    public void disableProcessVideoButton() {
        processVideo_Button.setDisable(true);
    }

    public void enableProcessVideoButton() {
        processVideo_Button.setDisable(false);
    }

    public boolean processVideoButtonIsDisabled() {
        return processVideo_Button.isDisabled();
    }

    public void playVideoToPane(File videoFile) throws Exception
    {
        String uri = videoFile.toURI().toString();
        Media media = new Media(uri);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);
        playInputVideo_Pane.getChildren().add(mediaView);
        mediaView.fitHeightProperty().bind(playInputVideo_Pane.heightProperty());
        mediaView.fitWidthProperty().bind(playInputVideo_Pane.widthProperty());
        mediaPlayer.play();
    }

    public void appendToLog(String message) {
        log_Area.setText(log_Area.getText() + message + "\n");
    }

    public void changeVideoNameLabel(String filename) {
        videoName.setText(filename);
    }

    public void disableChooseVideoFileButton() {
        chooseVideoFile_Button.setDisable(true);
    }

    public void enableChooseVideoFileButton() {
        chooseVideoFile_Button.setDisable(false);
    }

    public void updateProgressBar(double progress) {
        progressBar1.setProgress(progress);
    }

    public void updateProgressIndicator(double progress) {
        progressIndicator.setProgress(progress);
    }

    public void appendToExtractedTextArea(String text) {
        extractedText_Area.setText(extractedText_Area.getText() + text + "\n");
    }

    public void testFXClicked(MouseEvent mouseEvent) {

    }

    public static File getFilename() {
        return filename;
    }
}


