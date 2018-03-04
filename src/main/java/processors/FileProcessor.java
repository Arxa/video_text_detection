package processors;

import controllers.MainController;
import entities.ApplicationPaths;
import entities.Controllers;
import entities.OutputFolderNames;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
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
    public static boolean validateVideoFile(File videoFile) throws Exception
    {
        if (videoFile == null) return false;
        if (!FileProcessor.validateVideoFileName(videoFile)){
            throw new Exception("Cannot load the specified file");
        }
        MainController.setCurrentVideoFile(videoFile);
        MainController.resizeStageSlowly(1200, true);
        return true;
    }

    /**
     * Creates the required directories for the application to work,
     * by setting a unique directory name for the current application use
     * and creating directories required by the application.
     * @return True if successful, False otherwise
     */
    public static void createDirectories(File chosenFile) throws IOException {
        // Generating unique name of current video file operation
        ApplicationPaths.UNIQUE_OUTPUT_FOLDER_NAME = chosenFile.getName().replace(".mp4","")+" "+
                new Date().toString().replace(":","-");
        // Creating directories for application outputs
        Files.createDirectories(Paths.get(ApplicationPaths.RESOURCES_OUTPUTS,ApplicationPaths.UNIQUE_OUTPUT_FOLDER_NAME, OutputFolderNames.ocr_images.name()));
        Files.createDirectories(Paths.get(ApplicationPaths.RESOURCES_OUTPUTS,ApplicationPaths.UNIQUE_OUTPUT_FOLDER_NAME, OutputFolderNames.detected_areas.name()));
        Files.createDirectories(Paths.get(ApplicationPaths.RESOURCES_OUTPUTS,ApplicationPaths.UNIQUE_OUTPUT_FOLDER_NAME, OutputFolderNames.detection_steps.name()));
        Files.createDirectories(Paths.get(ApplicationPaths.RESOURCES_OUTPUTS,ApplicationPaths.UNIQUE_OUTPUT_FOLDER_NAME, OutputFolderNames.video.name()));
        Files.createDirectories(Paths.get(ApplicationPaths.RESOURCES_OUTPUTS,ApplicationPaths.UNIQUE_OUTPUT_FOLDER_NAME, OutputFolderNames.ocr_preprocessing.name()));
        Files.createDirectories(Paths.get(ApplicationPaths.RESOURCES_OUTPUTS,ApplicationPaths.UNIQUE_OUTPUT_FOLDER_NAME, OutputFolderNames.filter2_too_small.name()));
        Files.createDirectories(Paths.get(ApplicationPaths.RESOURCES_OUTPUTS,ApplicationPaths.UNIQUE_OUTPUT_FOLDER_NAME, OutputFolderNames.filter1_too_high.name()));
        Files.createDirectories(Paths.get(ApplicationPaths.RESOURCES_OUTPUTS,ApplicationPaths.UNIQUE_OUTPUT_FOLDER_NAME, OutputFolderNames.filter3_svm_no_text.name()));
        Files.createDirectories(Paths.get(ApplicationPaths.RESOURCES_OUTPUTS,ApplicationPaths.UNIQUE_OUTPUT_FOLDER_NAME, OutputFolderNames.svm_has_text.name()));
    }

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
    public static void loadLibraries() throws Exception
    {
        if(SystemUtils.IS_OS_WINDOWS)
        {
            int bit = Integer.parseInt(System.getProperty("sun.arch.data.model"));
            if(bit == 32){
                System.load(Paths.get(ApplicationPaths.RESOURCES_NATIVES,"opencv_320_32.dll").toString());
                Controllers.getLogController().logTextArea.appendText("Loaded OpenCV for Windows 32 bit\n");

                System.load(Paths.get(ApplicationPaths.RESOURCES_NATIVES,"opencv_ffmpeg320_32.dll").toString());
                Controllers.getLogController().logTextArea.appendText("Loaded FFMPEG for Windows 32 bit\n");

                System.load(Paths.get(ApplicationPaths.RESOURCES_NATIVES,"openh264-1.6.0-win32msvc.dll").toString());
                Controllers.getLogController().logTextArea.appendText("Loaded OpenH264 for Windows 32 bit\n");
            }
            else if (bit == 64){
                System.load(Paths.get(ApplicationPaths.RESOURCES_NATIVES,"opencv_java320_64.dll").toString());
                Controllers.getLogController().logTextArea.appendText("Loaded OpenCV for Windows 64 bit\n");

                System.load(Paths.get(ApplicationPaths.RESOURCES_NATIVES,"opencv_ffmpeg320_64.dll").toString());
                Controllers.getLogController().logTextArea.appendText("Loaded FFMPEG for Windows 64 bit\n");

                System.load(Paths.get(ApplicationPaths.RESOURCES_NATIVES,"openh264-1.6.0-win64msvc.dll").toString());
                Controllers.getLogController().logTextArea.appendText("Loaded OpenH264 for Windows 64 bit\n");
            }
            else{
                Controllers.getLogController().logTextArea.appendText("Unknown Windows bit - trying with 32");
                System.load(Paths.get(ApplicationPaths.RESOURCES_NATIVES,"opencv_java320_32.dll").toString());
                Controllers.getLogController().logTextArea.appendText("Loaded OpenCV for Windows 32 bit\n");
                System.load(Paths.get(ApplicationPaths.RESOURCES_NATIVES,"openh264-1.6.0-win32msvc.dll").toString());
                Controllers.getLogController().logTextArea.appendText("Loaded OpenH264 for Windows 32 bit\n");
            }
        }
        else if(SystemUtils.IS_OS_MAC){
            Controllers.getLogController().logTextArea.appendText("This version os the application cannot run on MAC OS yet."+"\n");
        }
        else if(SystemUtils.IS_OS_LINUX){
            int bit = Integer.parseInt(System.getProperty("sun.arch.data.model"));
            if (bit == 32){
                //todo add support
                Controllers.getLogController().logTextArea.appendText("32-bit Linux not supported yet\n");
            }
            else if (bit == 64){
                System.load(Paths.get(ApplicationPaths.RESOURCES_NATIVES,"libopencv_320_64.so").toString());
                Controllers.getLogController().logTextArea.appendText("Loaded OpenCV for Linux 64 bit\n");

                System.load(Paths.get(ApplicationPaths.RESOURCES_NATIVES,"libopenh264-1.6.0-linux64.3.so").toString());
                Controllers.getLogController().logTextArea.appendText("Loaded OpenH264 for Linux 64 bit\n");
            }
            else {
                Controllers.getLogController().logTextArea.appendText("Unknown Linux bit - trying with 32\n");
                Controllers.getLogController().logTextArea.appendText("OS not supported yet\n");
            }
        }
    }

}