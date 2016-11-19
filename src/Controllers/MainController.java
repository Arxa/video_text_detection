package Controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.io.File;

public class MainController
{
    // View Elements
    public Pane pane;
    public Pane pane2;
    public Pane pane3;
    public Pane pane4;
    public Button grab;
    public Label label1;


    public MainController() {
    }

    public void loadVideo(ActionEvent actionEvent)
    {
        VideoPlayer.loadLocalVideo(label1, pane);
    }

    public void grabFrame(ActionEvent actionEvent)
    {
        FrameProcessor.grabFrames();
    }

    public void initializeFrames(ActionEvent actionEvent)
    {
        FrameProcessor.getInitialFrames(pane2, pane3);
    }
}


