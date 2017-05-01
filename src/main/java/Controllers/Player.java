package Controllers;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
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

    public static void loadAndPlayVideo(Pane pane, TextArea logArea, Button processButton,
                                        Button chooseVideoFileButton, Label videoNameLabel)
    {
        processButton.setDisable(true);

        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose video file");
        filename = fileChooser.showOpenDialog(stage);

        if (filename == null) {
            return;
        }

        if (filename.exists())
        {
            try
            {
                playVideoToPane(pane,filename);
                Writer.setUniqueFolderName(filename.getName().replace(".mp4","")+" "+
                        new Date().toString().replace(":","-"));

                Files.createDirectories(Paths.get(Writer.getFolderPath()
                        + Writer.getUniqueFolderName() + "\\Text Blocks"));
                Files.createDirectories(Paths.get(Writer.getFolderPath()
                        + Writer.getUniqueFolderName() + "\\Painted Frames"));

                Log.printLogMessageToGUI(logArea, "Video file loaded successfully");
                ViewController.updateVideoNameLabel(filename.getName(), videoNameLabel);
                processButton.setDisable(false);
            }
            catch (java.lang.RuntimeException | IOException ex)
            {
                Log.printLogMessageToGUI(logArea, "File format not supported: please use an .mp4 format");
            }
        }
        else
        {
            Log.printLogMessageToGUI(logArea, "File does not exist!");
        }
    }

//    public static void playProcessedVideo(Pane pane,String filePath)
//    {
//        File file = new File(filePath);
//        if (file.exists())
//        {
//            try {
//                playVideoToPane(pane,file);
//            }
//            catch (java.lang.RuntimeException ex) {
//                System.out.println("ERROR");
//            }
//        }
//        else System.out.println("PROCESSED FILE DOES NOT EXIST");
//    }

    public static void playVideoToPane(Pane pane, File filePath)
    {
        String uri = filePath.toURI().toString();
        Media media = new Media(uri);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);
        pane.getChildren().add(mediaView);
        mediaView.fitHeightProperty().bind(pane.heightProperty());
        mediaView.fitWidthProperty().bind(pane.widthProperty());
        mediaPlayer.play();
    }

    public static File getFilename() {
        return filename;
    }
}
