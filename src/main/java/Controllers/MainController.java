package Controllers;

import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MainController
{
    public Pane videoPane;
    public ImageView videoIcon;
    public TextArea textArea;
    public Button extractButton;
    public ProgressIndicator progressIndicator;
    public AnchorPane anchorPane;
    public MenuItem openVideo;
    public MenuItem closeVideo;
    public MenuItem playOriginal;
    public MenuItem playDetected;

    private static File currentVideoFile;
    private static Stage stage;

    public void initialize()
    {
        videoIcon.setCursor(Cursor.HAND);
        extractButton.setCursor(Cursor.HAND);
        videoIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            FileProcessor.chooseVideoFile();
        });
        extractButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            extractButton.setVisible(false);
            progressIndicator.setVisible(true);
            progressIndicator.setProgress(-1.0);
            VideoProcessor.extractText(currentVideoFile);
        });
        openVideo.setOnAction(event -> {
            textArea.clear();
            progressIndicator.setVisible(false);
            FileProcessor.chooseVideoFile();
        });
        closeVideo.setOnAction(event -> {
            textArea.clear();
            videoPane.getChildren().clear();
            videoPane.getChildren().add(videoIcon);
            videoIcon.setVisible(true);
            resizeStageSlowly(680.0, false);
        });
        playOriginal.setOnAction(event -> {
            Player.playVideo(currentVideoFile);
        });
        playDetected.setOnAction(event -> {
            File videoFile = new File(ImageWriter.getUniquePath()+"\\Video\\video.mp4");
            if (videoFile.exists()){
                Player.playVideo(videoFile);
            }
        });
    }

    public static File getCurrentVideoFile() {
        return currentVideoFile;
    }

    public static void setCurrentVideoFile(File currentVideoFile) {
        MainController.currentVideoFile = currentVideoFile;
    }
    public static void setStage(Stage stage1){
        stage = stage1;
    }

    public static Stage getStage() {
        return stage;
    }


    public static void resizeStageSlowly(double desiredSize, boolean maximize){
        References.getMainController().extractButton.setVisible(true);
        Timer animTimer = new Timer();
        animTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (maximize){
                    if ((int)MainController.getStage().getWidth() < (int)desiredSize) {
                        MainController.getStage().setWidth(MainController.getStage().getWidth() + 5.0);
                    }else {
                        MainController.getStage().setWidth(desiredSize);
                        this.cancel();
                    }
                } else {
                    if ((int)MainController.getStage().getWidth() > (int)desiredSize) {
                        MainController.getStage().setWidth(MainController.getStage().getWidth() - 5.0);
                    } else {
                        MainController.getStage().setWidth(desiredSize);
                        this.cancel();
                    }
                }
            }
        },1, 5);
    }
}


