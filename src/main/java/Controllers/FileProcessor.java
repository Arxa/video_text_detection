package Controllers;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by arxa on 16/11/2016.
 */

public class FileProcessor
{
    public static void chooseVideoFile()
    {
        MainController.setCurrentVideoFile(FileProcessor.showFileDialog());
        if (!FileProcessor.validateVideoFileName(MainController.getCurrentVideoFile())){
            References.getMainController().textArea.appendText("ERROR: Failed to find video file");
            return;
        }
        if (!Player.playVideo(MainController.getCurrentVideoFile())){
            References.getMainController().textArea.appendText("ERROR: Failed to play video file");
            return;
        }
        if (!FileProcessor.createDirectories()){
            References.getMainController().textArea.appendText("ERROR: Failed to create directories");
            return;
        }
        MainController.resizeStageSlowly(1200.0, true);
    }

    public static boolean createDirectories()
    {
        try {
            /*
            Generating unique name of current video file operation
             */
            ImageWriter.setUniqueFolderName(MainController.getCurrentVideoFile().getName().replace(".mp4","")+" "+
                    new Date().toString().replace(":","-"));
            /*
            Creating paths for write operations later
             */
            Files.createDirectories(Paths.get(ImageWriter.getFolderPath()
                    + ImageWriter.getUniqueFolderName() + "\\Text Blocks"));
            Files.createDirectories(Paths.get(ImageWriter.getFolderPath()
                    + ImageWriter.getUniqueFolderName() + "\\Painted Frames"));
            Files.createDirectories(Paths.get(ImageWriter.getFolderPath()
                    + ImageWriter.getUniqueFolderName() + "\\Steps"));
            Files.createDirectories(Paths.get(ImageWriter.getFolderPath()
                    + ImageWriter.getUniqueFolderName() + "\\Video"));
            return true;
        }
        catch (RuntimeException | IOException ex) {
            return  false;
        }
    }

    public static boolean validateVideoFileName(File filename) {
        return filename != null && filename.exists();
    }

    public static File showFileDialog()
    {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose video file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter(
                "Video Files", "*.avi"));
        return fileChooser.showOpenDialog(stage);
    }

    public static void loadLibraries()
    {
        System.load("C:\\Users\\310297685\\IdeaProjects\\Thesis\\VideoText_Extractor\\src\\main\\resources\\DLLs\\openh264-1.6.0-win64msvc.dll");

        try {
            String osName = System.getProperty("os.name");
            if(osName.startsWith("Windows"))
            {
                int bit = Integer.parseInt(System.getProperty("sun.arch.data.model"));
                if(bit == 32){
                    References.getMainController().textArea.appendText("Loaded OpenCV for "+osName+" "+bit+"-bit\n");
                    System.load("C:\\Users\\310297685\\IdeaProjects\\Thesis\\VideoText_Extractor\\src\\main\\resources\\DLLs\\opencv_java320.dll");
                }
                else if (bit == 64){
                    References.getMainController().textArea.appendText("Loaded OpenCV for "+osName+" "+bit+"-bit\n");
                    System.load("C:\\Users\\310297685\\IdeaProjects\\Thesis\\VideoText_Extractor\\src\\main\\resources\\DLLs\\opencv_java320.dll");
                }
                else{
                    References.getMainController().textArea.appendText("Loaded OpenCV for "+osName+" "+bit+"-bit\n");
                    System.load("C:\\Users\\310297685\\IdeaProjects\\Thesis\\VideoText_Extractor\\src\\main\\resources\\DLLs\\opencv_java320.dll");
                }
            }
            else if(osName.equals("Mac OS X")){
                References.getMainController().textArea.appendText("Loaded OpenCV for "+osName);
                System.load("C:\\Users\\310297685\\IdeaProjects\\Thesis\\VideoText_Extractor\\src\\main\\resources\\DLLs\\opencv_java320.dll");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load opencv native library\n", e);
        }
    }
}