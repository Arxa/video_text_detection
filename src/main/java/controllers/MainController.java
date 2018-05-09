package controllers;

import entities.ApplicationPaths;
import entities.Controllers;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
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

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

import static java.awt.Desktop.getDesktop;

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
    public MenuItem outputImagesMenuItem;

    private static File currentVideoFile = new File("nofile");
    private static Stage mainStage;

    public void initialize()
    {
        Controllers.setMainController(this);
        initializeViews();

        textArea.setVisible(false); // TestFx will fail otherwise for some reason
        videoIcon.setCursor(Cursor.HAND);
        processButton.setCursor(Cursor.HAND);

        outputImagesMenuItem.setOnAction(event -> {
            if( Desktop.isDesktopSupported() )
            {
                new Thread(() -> {
                    try {
                        Desktop.getDesktop().open(new File(ApplicationPaths.RESOURCES_OUTPUTS));
                    } catch (IOException e) {
                        showException(e);
                    }
                }).start();
            }
        });

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
            File dragFile = event.getDragboard().getFiles().get(0);
            loadThisFile(dragFile);
            event.setDropCompleted(true);
            event.consume();
        });

        logMenuItem.setOnAction(event -> {
            LogController.showLogStage();
        });

        settingsMenuItem.setOnAction(event -> {
            SettingsController.showSettingsStage();
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
            progressBar.setVisible(true);
            textArea.setVisible(true);
            increaseFont_button.setVisible(true);
            decreaseFont_button.setVisible(true);
            textArea.clear();
            VideoProcessor.checkThreadStatus();
            VideoProcessor.processVideoFile(currentVideoFile);
        });

        openVideo.setOnAction(event -> {
            openVideoFile();
        });

        closeVideo.setOnAction(event -> {
            VideoProcessor.checkThreadStatus();
            Player.stopMediaPlayer();
            ApplicationPaths.UNIQUE_OUTPUT_FOLDER_NAME = "";
            setCurrentVideoFile(new File("nofile"));
            videoPane.getChildren().clear();
            videoPane.getChildren().add(videoIcon);
            videoIcon.setVisible(true);
            increaseFont_button.setVisible(false);
            decreaseFont_button.setVisible(false);
            textArea.clear();
            textArea.setVisible(false);
            progressIndicator.setVisible(false);
            progressBar.setVisible(false);
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
                    if ((int)mainStage.getWidth() < (int)desiredSize) {
                        mainStage.setWidth(mainStage.getWidth() + 5.0);
                    }else {
                        mainStage.setWidth(desiredSize);
                        this.cancel();
                    }
                } else {
                    if ((int)mainStage.getWidth() > (int)desiredSize) {
                        mainStage.setWidth(mainStage.getWidth() - 5.0);
                    } else {
                        mainStage.setWidth(desiredSize);
                        this.cancel();
                    }
                }
            }
        },1, 5);
    }

    /**
     * Initializes FXML loaders and controllers of other views
     */
    public void initializeViews(){
        try {
            FXMLLoader logLoader = new FXMLLoader(Paths.get(ApplicationPaths.RESOURCES_VIEWS,"log.fxml").toFile().toURI().toURL());
            Parent logRoot = logLoader.load();
            Stage logStage = new Stage();
            logStage.setTitle("Log");
            logStage.getIcons().add(new Image(Paths.get(ApplicationPaths.RESOURCES_ICONS,"app.png").toFile().toURI().toURL().toString()));
            logStage.setScene(new Scene(logRoot, 300, 400));
            logStage.setMinWidth(300);
            logStage.setMinHeight(200);
            LogController.setLogStage(logStage);
        } catch (IOException e) {
            showError("Failed to initialize Log view; "+e.getMessage());
            return;
        }

        try {
            FXMLLoader settingsLoader = new FXMLLoader(Paths.get(ApplicationPaths.RESOURCES_VIEWS,"settings.fxml").toFile().toURI().toURL());
            Parent settingsRoot = settingsLoader.load();
            Stage settingsStage = new Stage();
            settingsStage.setTitle("Settings");
            settingsStage.getIcons().add(new Image(Paths.get(ApplicationPaths.RESOURCES_ICONS,"app.png").toFile().toURI().toURL().toString()));
            settingsStage.setScene(new Scene(settingsRoot, 400, 300));
            settingsStage.setResizable(false);
            SettingsController.setSettingsStage(settingsStage);
        } catch (IOException e) {
            showError("Failed to initialize Settings view; "+e.getMessage());
        }
    }

    public static void showException(Exception e){
        Platform.runLater(()->{
            Controllers.getLogController().logTextArea.appendText("Exception caught: " + e+"\n");
            LogController.showLogStage();
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
            LogController.showLogStage();
        });
    }

    public void openVideoFile(){
        File videoFile = FileProcessor.showFileDialog();
        loadThisFile(videoFile);
    }

    public void loadThisFile(File file){
        try {
            if (FileProcessor.validateVideoFile(file)){
                VideoProcessor.checkThreadStatus();
                textArea.clear();
                progressIndicator.setVisible(false);
                progressBar.setVisible(false);
                increaseFont_button.setVisible(false);
                decreaseFont_button.setVisible(false);
                Player.playVideo(file);
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

}
