package ViewControllers;

import Entities.Controllers;
import Processors.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.opencv.video.Video;

import java.io.File;
import java.io.IOException;
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
    public MenuItem preferencesMenuItem;
    public ProgressBar progressBar;

    public void initialize()
    {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(new File("C:\\Users\\310297685\\IdeaProjects\\Thesis\\VideoText_Extractor\\src\\main\\resources\\Views\\preferences.fxml").toURI().toURL());
            root = loader.load();
            Controllers.setPreferencesController(loader);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Stage stage = new Stage();
        stage.setTitle("Preferences");
        stage.setScene(new Scene(root, 600, 250));

        videoIcon.setCursor(Cursor.HAND);
        extractButton.setCursor(Cursor.HAND);

        preferencesMenuItem.setOnAction(event -> {
            stage.show();
        });

        videoIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (VideoProcessor.getThread() != null){
                if (VideoProcessor.getThread().isAlive()){
                    VideoProcessor.getThread().interrupt();
                }
            }
            textArea.clear();
            progressIndicator.setVisible(false);
            FileProcessor.chooseVideoFile();
        });
        extractButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (VideoProcessor.getThread() != null){
                if (VideoProcessor.getThread().isAlive()){
                    VideoProcessor.getThread().interrupt();
                }
            }
            extractButton.setVisible(false);
            progressIndicator.setVisible(true);
            progressIndicator.setProgress(-1.0);
            progressBar.setVisible(true);
            textArea.clear();
            VideoProcessor.processVideoFile(currentVideoFile);
        });
        openVideo.setOnAction(event -> {
            if (VideoProcessor.getThread() != null){
                if (VideoProcessor.getThread().isAlive()){
                    VideoProcessor.getThread().interrupt();
                }
            }
            progressIndicator.setVisible(false);
            textArea.clear();
            FileProcessor.chooseVideoFile();
        });
        closeVideo.setOnAction(event -> {
            if (VideoProcessor.getThread() != null){
                if (VideoProcessor.getThread().isAlive()){
                    VideoProcessor.getThread().interrupt();
                }
            }
            videoPane.getChildren().clear();
            videoPane.getChildren().add(videoIcon);
            videoIcon.setVisible(true);
            resizeStageSlowly(680.0, false);
            textArea.clear();
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
        Controllers.getMainController().extractButton.setVisible(true);
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


