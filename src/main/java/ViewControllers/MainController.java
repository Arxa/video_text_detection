package ViewControllers;

import Entities.ApplicationPaths;
import Entities.Controllers;
import Processors.FileProcessor;
import Processors.Player;
import Processors.VideoProcessor;
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
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
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
        ApplicationPaths.setApplicationPaths();
        initializeViews();
        ApplicationPaths.checkCaller();

        videoIcon.setCursor(Cursor.HAND);
        extractButton.setCursor(Cursor.HAND);

        // When dragging something, allow it to be copied/moved only if it's a mp4 file
        videoPane.setOnDragOver(event -> {
            if (event.getDragboard().getFiles().get(0).isFile()){
                if (FilenameUtils.getExtension(event.getDragboard().getFiles().get(0).getPath()).equals("mp4")) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
            }
            event.consume();
        });

        // Once the drop is complete (only permitted drags with reach this point)
        videoPane.setOnDragDropped(event -> {
            // Kill existing thread first (if there is one)
            if (VideoProcessor.getThread() != null){
                if (VideoProcessor.getThread().isAlive()){
                    VideoProcessor.getThread().interrupt();
                }
            }
            textArea.clear();
            progressIndicator.setVisible(false);
            FileProcessor.validateVideoFile(event.getDragboard().getFiles().get(0));
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
            // Kill existing thread first (if there is one)
            if (VideoProcessor.getThread() != null){
                if (VideoProcessor.getThread().isAlive()){
                    VideoProcessor.getThread().interrupt();
                }
            }
            textArea.clear();
            progressIndicator.setVisible(false);
            FileProcessor.validateVideoFile(FileProcessor.showFileDialog());
        });

        extractButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            // Kill existing thread first (if there is one)
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
            // Kill existing thread first (if there is one)
            if (VideoProcessor.getThread() != null){
                if (VideoProcessor.getThread().isAlive()){
                    VideoProcessor.getThread().interrupt();
                }
            }
            videoPane.getChildren().clear();
            progressIndicator.setVisible(false);
            textArea.clear();
            FileProcessor.validateVideoFile(FileProcessor.showFileDialog());
        });

        closeVideo.setOnAction(event -> {
            // Kill existing thread first (if there is one)
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
            File videoFile = Paths.get(ApplicationPaths.RESOURCES_OUTPUTS, ApplicationPaths.UNIQUE_FOLDER_NAME,
                    "Video", "video.mp4").toFile();
            if (videoFile.exists()){
                Player.playVideo(videoFile);
            }
        });
    }

    /**
     * Applies a GUI resizing animation both for expanding and collapsing the window
     * @param desiredSize The desired width of application's stage
     * @param maximize Flag that indicates whether to expand or collapse the stage's width
     */
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
        settingsStage.setScene(new Scene(root, 400, 250));
    }

    @Contract(pure = true)
    public static File getCurrentVideoFile() {
        return currentVideoFile;
    }

    public static void setCurrentVideoFile(File currentVideoFile) {
        MainController.currentVideoFile = currentVideoFile;
    }
    public static void setMainStage(Stage stage1){
        mainStage = stage1;
    }

    @Contract(pure = true)
    public static Stage getMainStage() {
        return mainStage;
    }

    @Contract(pure = true)
    public static Stage getLogStage() {
        return logStage;
    }

}
