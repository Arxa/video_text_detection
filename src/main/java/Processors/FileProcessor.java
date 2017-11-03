package Processors;

import Entities.ApplicationPaths;
import Entities.Caller;
import Entities.Controllers;
import ViewControllers.MainController;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.Contract;
import org.opencv.core.Core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Created by arxa on 16/11/2016.
 */

public class FileProcessor
{
    public static Node node;
    /**
     * Allows the user to choose a file through a file dialog
     * and then validates if the File is valid, if it's playable
     * and if the corresponding directories have been successfully created.
     * If no problem appears, the application window resizes slowly.
     */
    public static void validateVideoFile(File videoFile)
    {
        if (videoFile == null) return;
        Alert alert;
        if (!FileProcessor.validateVideoFileName(videoFile)){
            alert = new Alert(Alert.AlertType.WARNING, "ERROR on loading file\n"+
                    "Couldn't load file specified", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        if (!Player.playVideo(videoFile)){
            alert = new Alert(Alert.AlertType.WARNING, "ERROR on playing the video file",ButtonType.OK);
            alert.showAndWait();
            return;
        }

        MainController.resizeStageSlowly(1150, true);
        MainController.setCurrentVideoFile(videoFile);
        Controllers.getMainController().progressIndicator.setVisible(false);
        Controllers.getMainController().progressBar.setVisible(false);
        Controllers.getMainController().extractButton.setVisible(true);
        Controllers.getMainController().textArea.setVisible(true);
        Controllers.getMainController().textArea.clear();
    }

    /**
     * Creates the required directories for the application to work,
     * by setting a unique directory name for the current application use
     * and creating directories required by the application.
     * @return True if successful, False otherwise
     */
    public static boolean createDirectories(File chosenFile)
    {
        try {
            // Generating unique name of current video file operation
            ApplicationPaths.UNIQUE_FOLDER_NAME = chosenFile.getName().replace(".mp4","")+" "+
                    new Date().toString().replace(":","-");
            // Creating directories for application outputs
            Files.createDirectories(Paths.get(ApplicationPaths.RESOURCES_OUTPUTS,ApplicationPaths.UNIQUE_FOLDER_NAME, "Text Blocks"));
            Files.createDirectories(Paths.get(ApplicationPaths.RESOURCES_OUTPUTS,ApplicationPaths.UNIQUE_FOLDER_NAME, "Painted Frames"));
            Files.createDirectories(Paths.get(ApplicationPaths.RESOURCES_OUTPUTS,ApplicationPaths.UNIQUE_FOLDER_NAME, "Steps"));
            Files.createDirectories(Paths.get(ApplicationPaths.RESOURCES_OUTPUTS,ApplicationPaths.UNIQUE_FOLDER_NAME,"Video"));
            Files.createDirectories(Paths.get(ApplicationPaths.RESOURCES_OUTPUTS,ApplicationPaths.UNIQUE_FOLDER_NAME, "OCR Images"));
            Files.createDirectories(Paths.get(ApplicationPaths.RESOURCES_OUTPUTS,ApplicationPaths.UNIQUE_FOLDER_NAME, "Temp"));
            return true;
        }
        catch (RuntimeException | IOException ex) {
            return false;
        }
    }

    @Contract("null -> false")
    public static boolean validateVideoFileName(File filename) {
        return filename != null && filename.exists() && filename.canRead()
                && FilenameUtils.getExtension(filename.getPath()).equalsIgnoreCase("mp4");
    }

    /**
     * Lunches the GUI file chooser only for .mp4 files
     * @return The File that was chosen or null otherwise
     */
    public static File showFileDialog()
    {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose video file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Video Files", "*.mp4"));
        return fileChooser.showOpenDialog(stage);
    }

    /**
     * Loads Native Libraries for the detected OS
     */
    public static void loadLibraries()
    {
        setLibraryPath();
        try {
            if(SystemUtils.IS_OS_WINDOWS)
            {
                int bit = Integer.parseInt(System.getProperty("sun.arch.data.model"));
                if(bit == 32){
                    System.loadLibrary("opencv_320_32");
                    Controllers.getLogController().logTextArea.appendText("Loaded OpenCV for Windows 32 bit\n");
                    System.loadLibrary("openh264-1.6.0-win32msvc");
                    Controllers.getLogController().logTextArea.appendText("Loaded OpenH264 for Windows 32 bit\n");
                }
                else if (bit == 64){
                    System.loadLibrary("opencv_ffmpeg320_64");
                    Controllers.getLogController().logTextArea.appendText("Loaded FFMPEG for Windows 64 bit\n");
                    System.loadLibrary("opencv_320_64");
                    Controllers.getLogController().logTextArea.appendText("Loaded OpenCV for Windows 64 bit\n");
                    System.loadLibrary("openh264-1.6.0-win64msvc");
                    Controllers.getLogController().logTextArea.appendText("Loaded OpenH264 for Windows 64 bit\n");
                }
                else{
                    Controllers.getLogController().logTextArea.appendText("Unknown Windows bit - trying with 32");
                    System.loadLibrary("opencv_320_32");
                    Controllers.getLogController().logTextArea.appendText("Loaded OpenCV for Windows 32 bit\n");
                    System.loadLibrary("openh264-1.6.0-win32msvc");
                    Controllers.getLogController().logTextArea.appendText("Loaded OpenH264 for Windows 32 bit\n");
                }
            }
            else if(SystemUtils.IS_OS_MAC){
                Controllers.getLogController().logTextArea.appendText("This version os the application cannot run on MAC OS yet."+"\n");
            }
            else if(SystemUtils.IS_OS_LINUX){
                int bit = Integer.parseInt(System.getProperty("sun.arch.data.model"));
                if (bit == 32){
                    Controllers.getLogController().logTextArea.appendText("OS not supported yet\n");
                }
                else if (bit == 64){
                    System.loadLibrary("opencv_320_64");
                    Controllers.getLogController().logTextArea.appendText("Loaded OpenCV for Linux 64 bit\n");
                    System.loadLibrary("openh264-1.6.0-linux64.3");
                    Controllers.getLogController().logTextArea.appendText("Loaded OpenH264 for Linux 64 bit\n");
                }
                else {
                    Controllers.getLogController().logTextArea.appendText("Unknown Linux bit - trying with 32\n");
                    Controllers.getLogController().logTextArea.appendText("OS not supported yet\n");
                }
            }
        } catch (Throwable e) {
            Controllers.getLogController().logTextArea.appendText("Failed to load native libraries: " + e.getMessage()+"\n");
            System.out.println(e.getMessage());
            MainController.getLogStage().show();
            new Alert(Alert.AlertType.ERROR, "Failed to locate Native files!").showAndWait();
            Platform.exit();
        }
    }

    private static void setLibraryPath() {
        try {
            System.setProperty("java.library.path", ApplicationPaths.RESOURCES_NATIVES);
            Controllers.getLogController().logTextArea.appendText("JavaLibraryPath= " + ApplicationPaths.RESOURCES_NATIVES+"\n");
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Failed to set JavaLibraryPath!").showAndWait();
            Platform.exit();
        }
    }
}