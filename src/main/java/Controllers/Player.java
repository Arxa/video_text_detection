package Controllers;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Created by arxa on 16/11/2016.
 */

public class Player
{
    private static File filename;

    public static void updateDirectories(MainController view)
    {
        try
        {
            /*
            Generating unique name of current video file operation
             */
            Writer.setUniqueFolderName(filename.getName().replace(".mp4","")+" "+
                    new Date().toString().replace(":","-"));
            /*
            Creating paths for write operations later
             */
            Files.createDirectories(Paths.get(Writer.getFolderPath()
                    + Writer.getUniqueFolderName() + "\\Text Blocks"));
            Files.createDirectories(Paths.get(Writer.getFolderPath()
                    + Writer.getUniqueFolderName() + "\\Painted Frames"));
        }
        catch (RuntimeException | IOException ex) {
            view.appendToLog("File format not supported: please use .mp4 format");
        }
    }



    public static int validateFileName(File filename, MainController view)
    {
        if (filename == null) {
            /*
             No file was chosen with the choose file dialog
             Nothing should change in the GUI
              */
            return 0; // NULL
        }
        if (filename.exists()) {
            /*
            GUI update is not needed here
             */
            return 1; // FILE OK
        }
        else {
            view.appendToLog("File does not exist!");
            return -1; // FILE DOES NOT EXIST
        }
    }

    public static File getFilenameFromDialog(MainController view)
    {
        view.disableProcessVideoButton();
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose video file");
        filename = fileChooser.showOpenDialog(stage);
        return filename;
    }

    public static File getFilename() {
        return filename;
    }
}