package ViewControllers;

import Entities.Controllers;
import Processors.FileProcessor;
import Processors.ImageWriter;
import Processors.Player;
import Processors.VideoProcessor;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

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
    public MenuItem settingsMenuItem;
    public ProgressBar progressBar;
    public MenuItem logMenuItem;

    private static File currentVideoFile;
    private static Stage mainStage;
    private static Stage settingsStage;
    private static Stage logStage;

    public void initialize()
    {
        initializeViews();

        videoIcon.setCursor(Cursor.HAND);
        extractButton.setCursor(Cursor.HAND);

        logMenuItem.setOnAction(event -> {
            logStage.show();
        });

        settingsMenuItem.setOnAction(event -> {
            settingsStage.show();
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
            videoPane.getChildren().clear();
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
            resizeStageSlowly(680, false);
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

    public static void resizeStageSlowly(double desiredSize, boolean maximize){
        Controllers.getMainController().extractButton.setVisible(true);
        Timer animTimer = new Timer();
        animTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (maximize){
                    if ((int)MainController.getMainStage().getWidth() < (int)desiredSize) {
                        MainController.getMainStage().setWidth(MainController.getMainStage().getWidth() + 5.0);
                    }else {
                        MainController.getMainStage().setWidth(desiredSize);
                        this.cancel();
                    }
                } else {
                    if ((int)MainController.getMainStage().getWidth() > (int)desiredSize) {
                        MainController.getMainStage().setWidth(MainController.getMainStage().getWidth() - 5.0);
                    } else {
                        MainController.getMainStage().setWidth(desiredSize);
                        this.cancel();
                    }
                }
            }
        },1, 5);
    }

    public void initializeViews(){
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(new File("src\\main\\resources\\Views\\log.fxml").toURI().toURL());
            root = loader.load();
            Controllers.setLogController(loader);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        logStage = new Stage();
        logStage.setTitle("Log");
        logStage.getIcons().add(new Image(MainController.class.getResourceAsStream("../Icons/app.png")));
        logStage.setScene(new Scene(root, 300, 400));

        try {
            FXMLLoader loader = new FXMLLoader(new File("src\\main\\resources\\Views\\settings.fxml").toURI().toURL());
            root = loader.load();
            Controllers.setPreferencesController(loader);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        settingsStage = new Stage();
        settingsStage.setTitle("Preferences");
        settingsStage.getIcons().add(new Image(MainController.class.getResourceAsStream("../Icons/app.png")));
        settingsStage.setScene(new Scene(root, 600, 250));
    }

    public static File getCurrentVideoFile() {
        return currentVideoFile;
    }

    public static void setCurrentVideoFile(File currentVideoFile) {
        MainController.currentVideoFile = currentVideoFile;
    }
    public static void setMainStage(Stage stage1){
        mainStage = stage1;
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static Stage getLogStage() {
        return logStage;
    }
}
