package Processors;

import Entities.ApplicationPaths;
import Entities.Controllers;
import ViewControllers.MainController;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.Contract;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Created by arxa on 16/11/2016.
 */

public class FileProcessor
{
    /**
     * Allows the user to choose a file through a file dialog
     * and then validates if the File is valid, if it's playable
     * and if the corresponding directories have been successfully created.
     * If no problem appears, the application window resizes slowly.
     */
    public static void validateVideoFile(File videoFile)
    {
        if (videoFile == null) return;
        if (!FileProcessor.validateVideoFileName(videoFile)){
            new Alert(Alert.AlertType.WARNING, "ERROR on loading file\n"+
                    "Couldn't load file specified", ButtonType.OK).showAndWait();
            return;
        }
        if (!Player.playVideo(videoFile)){
            new Alert(Alert.AlertType.WARNING, "ERROR on playing the video file\n"+
                    "Please choose a valid .mp4 video file", ButtonType.OK).showAndWait();
            return;
        }
        if (!FileProcessor.createDirectories(videoFile)){
            new Alert(Alert.AlertType.WARNING, "ERROR on creating directories\n"+
                    "Failed to create directories", ButtonType.OK).showAndWait();
            return;
        }
        MainController.setCurrentVideoFile(videoFile);
        MainController.resizeStageSlowly(1150, true);
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

            // Creating paths for write operations
            Files.createDirectories(Paths.get(ApplicationPaths.FOLDER_PATH + ApplicationPaths.FILE_SEPERATOR
                    + ApplicationPaths.UNIQUE_FOLDER_NAME + ApplicationPaths.FILE_SEPERATOR + "Text Blocks"));
            Files.createDirectories(Paths.get(ApplicationPaths.FOLDER_PATH + ApplicationPaths.FILE_SEPERATOR
                    + ApplicationPaths.UNIQUE_FOLDER_NAME + ApplicationPaths.FILE_SEPERATOR + "Painted Frames"));
            Files.createDirectories(Paths.get(ApplicationPaths.FOLDER_PATH + ApplicationPaths.FILE_SEPERATOR
                    + ApplicationPaths.UNIQUE_FOLDER_NAME + ApplicationPaths.FILE_SEPERATOR +"Steps"));
            Files.createDirectories(Paths.get(ApplicationPaths.FOLDER_PATH + ApplicationPaths.FILE_SEPERATOR
                    + ApplicationPaths.UNIQUE_FOLDER_NAME + ApplicationPaths.FILE_SEPERATOR + "Video"));
            Files.createDirectories(Paths.get(ApplicationPaths.FOLDER_PATH + ApplicationPaths.FILE_SEPERATOR
                    + ApplicationPaths.UNIQUE_FOLDER_NAME + ApplicationPaths.FILE_SEPERATOR + "OCR Images"));
            return true;
        }
        catch (RuntimeException | IOException ex) {
            return false;
        }
    }

    @Contract("null -> false")
    public static boolean validateVideoFileName(File filename) {
        return filename != null && filename.exists();
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
    //TODO load natives cross platform
    public static void loadLibraries() throws IOException, URISyntaxException
    {
        String path = ApplicationPaths.RESOURCES_NATIVES;
        try {
            if(SystemUtils.IS_OS_WINDOWS)
            {
                int bit = Integer.parseInt(System.getProperty("sun.arch.data.model"));
                if(bit == 32){
                    System.load(path + "\\opencv_java320.dll");
                    Controllers.getLogController().logTextArea.appendText("Loaded OpenCV for Windows 32 bit\n");
                    System.load(path + "\\openh264-1.7.0-win32.dll");
                    Controllers.getLogController().logTextArea.appendText("Loaded OpenH264 for Windows 32 bit\n");
                }
                else if (bit == 64){
                    System.load(path + "\\opencv_java320.dll");
                    Controllers.getLogController().logTextArea.appendText("Loaded OpenCV for Windows 64 bit\n");
                    System.load(path + "\\openh264-1.7.0-win64.dll");
                    Controllers.getLogController().logTextArea.appendText("Loaded OpenH264 for Windows 64 bit\n");
                }
                else{
                    Controllers.getLogController().logTextArea.appendText("Unknown Windows bit - trying with 32");
                    System.load(path + "\\opencv_java320.dll");
                    Controllers.getLogController().logTextArea.appendText("Loaded OpenCV for Windows 32 bit\n");
                    System.load(path + "\\openh264-1.7.0-win32.dll");
                    Controllers.getLogController().logTextArea.appendText("Loaded OpenH264 for Windows 32 bit\n");
                }
            }
            else if(SystemUtils.IS_OS_MAC){
                Controllers.getLogController().logTextArea.appendText("This version os the application cannot run on MAC OS yet."+"\n");
            }
            else if(SystemUtils.IS_OS_LINUX){
                int bit = Integer.parseInt(System.getProperty("sun.arch.data.model"));
                if (bit == 32){
                    System.load(path + "/libopencv_java320.so");
                    Controllers.getLogController().logTextArea.appendText("Loaded OpenCV for Linux 32 bit\n");
                    System.load(path + "/libopenh264-1.7.0-linux32.4.so");
                    Controllers.getLogController().logTextArea.appendText("Loaded OpenH264 for Linux 32 bit\n");
                }
                else if (bit == 64){
                    System.load(path + "/libopencv_java320.so");
                    Controllers.getLogController().logTextArea.appendText("Loaded OpenCV for Linux 64 bit\n");
                    System.load(path + "/libopenh264-1.7.0-linux64.4.so");
                    Controllers.getLogController().logTextArea.appendText("Loaded OpenH264 for Linux 64 bit\n");
                }
                else {
                    Controllers.getLogController().logTextArea.appendText("Unknown Linux bit - trying with 32\n");
                    System.load(path + "/libopencv_java320.so");
                    Controllers.getLogController().logTextArea.appendText("Loaded OpenCV for Linux 32 bit\n");
                    System.load(path + "/libopenh264-1.7.0-linux32.4.so");
                    Controllers.getLogController().logTextArea.appendText("Loaded OpenH264 for Linux 32 bit\n");
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
}