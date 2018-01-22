package controllers;

import entities.ApplicationPaths;
import entities.Controllers;
import javafx.application.Platform;
import org.apache.commons.lang3.exception.ExceptionUtils;
import processors.FileProcessor;
import processors.Player;
import processors.VideoProcessor;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainController
{
    public Pane videoPane;
    public ImageView videoIcon;
    public TextArea textArea;
    public Button processButton;
    public ProgressIndicator progressIndicator;
    public AnchorPane anchorPane;
    public MenuItem openVideo;
    public MenuItem closeVideo;
    public MenuItem playOriginal;
    public MenuItem playProcessed;
    public MenuItem settingsMenuItem;
    public ProgressBar progressBar;
    public MenuItem logMenuItem;
    public Button increaseFont_button;
    public Button decreaseFont_button;

    private static File currentVideoFile;
    private static Stage mainStage;
    private static Stage settingsStage;
    private static Stage logStage;

    public void initialize()
    {
        ApplicationPaths.setApplicationPaths();
        initializeViews();

        textArea.setVisible(false); // TestFx will fail otherwise for some reason
        videoIcon.setCursor(Cursor.HAND);
        processButton.setCursor(Cursor.HAND);

        increaseFont_button.setOnMouseClicked(event -> {
            textArea.setFont(Font.font("Verdana", FontWeight.NORMAL, textArea.getFont().getSize()+2));
        });

        decreaseFont_button.setOnMouseClicked(event -> {
            textArea.setFont(Font.font("Verdana", FontWeight.NORMAL, textArea.getFont().getSize()-2));
        });

        // When dragging something, allow it to be copied/moved only if it's a mp4 file
        videoPane.setOnDragOver(event -> {
            if (event.getDragboard().getFiles().get(0).isFile()){
                if (FilenameUtils.getExtension(event.getDragboard().getFiles().get(0).getPath()).equalsIgnoreCase("mp4")){
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
            }
            event.consume();
        });

        // Once the drop is complete (only permitted drags with reach this point)
        videoPane.setOnDragDropped(event -> {
            openVideoFile();
            event.setDropCompleted(true);
            event.consume();
        });

        logMenuItem.setOnAction(event -> {
            logStage.show();
        });

        settingsMenuItem.setOnAction(event -> {
            settingsStage.show();
        });

        videoIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            openVideoFile();
        });

        processButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                FileProcessor.createDirectories(currentVideoFile);
            } catch (IOException e) {
                showException(e);
                return;
            }
            processButton.setVisible(false);
            progressIndicator.setVisible(true);
            progressIndicator.setProgress(-1.0);
            progressBar.setVisible(true);
            textArea.setVisible(true);
            textArea.clear();
            VideoProcessor.checkThreadStatus();
            VideoProcessor.processVideoFile(currentVideoFile);
        });

        openVideo.setOnAction(event -> {
            openVideoFile();
        });

        closeVideo.setOnAction(event -> {
            VideoProcessor.checkThreadStatus();
            videoPane.getChildren().clear();
            videoPane.getChildren().add(videoIcon);
            videoIcon.setVisible(true);
            textArea.clear();
            textArea.setVisible(false);
            progressIndicator.setVisible(false);
            progressIndicator.setVisible(false);
            processButton.setVisible(false);
            resizeStageSlowly(680, false);
        });

        playOriginal.setOnAction(event -> {
            try {
                Player.playVideo(currentVideoFile);
            } catch (Exception e) {
                showException(e);
            }
        });

        playProcessed.setOnAction(event -> {
            try {
                Player.playProcessedVideo();
            } catch (Exception e) {
                showException(e);
            }
        });
    }

    /**
     * Applies a GUI resizing animation both for expanding and collapsing the window
     * @param desiredSize The desired width of application's stage
     * @param maximize Flag that indicates whether to expand or collapse the stage's width
     */
    public static void resizeStageSlowly(double desiredSize, boolean maximize){
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

    /**
     * Initializes FXML loaders and controllers of other Views
     */
    public void initializeViews(){
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Views/log.fxml"));
            root = loader.load();
            Controllers.setLogController(loader);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        logStage = new Stage();
        logStage.setTitle("Log");
        logStage.getIcons().add(new Image("file:src/main/resources/Icons/app.png"));
        logStage.setScene(new Scene(root, 300, 400));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Views/settings.fxml"));
            root = loader.load();
            Controllers.setSettingsController(loader);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        settingsStage = new Stage();
        settingsStage.setTitle("Settings");
        settingsStage.getIcons().add(new Image("file:src/main/resources/Icons/app.png"));
        settingsStage.setScene(new Scene(root, 400, 300));
    }

    public static void showException(Exception e){
        Platform.runLater(()->{
            Controllers.getLogController().logTextArea.appendText("Exception caught: " + e+"\n");
            MainController.getLogStage().show();
        });
    }

    public static void showInfo(String message){
        Platform.runLater(()->{
            Controllers.getLogController().logTextArea.appendText(message);
        });
    }

    public static void showError(String message){
        Platform.runLater(()->{
            Controllers.getLogController().logTextArea.appendText("ERROR: " + message);
            MainController.getLogStage().show();
        });
    }

    private void openVideoFile(){
        File videoFile = FileProcessor.showFileDialog();
        try {
            if (FileProcessor.validateVideoFile(videoFile)){
                VideoProcessor.checkThreadStatus();
                textArea.clear();
                videoPane.getChildren();
                progressIndicator.setVisible(false);
                progressBar.setVisible(false);
                Player.playVideo(videoFile);
                processButton.setVisible(true);
            }
        } catch (Exception e) {
            showException(e);
        }
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
