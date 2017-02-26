package Controllers;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;


/**
 * Created by arxa on 16/11/2016.
 */

public class Player
{
    private static File filename;

    public static void loadAndPlayVideo(Label label1, Pane pane)
    {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose video file");
        filename = fileChooser.showOpenDialog(stage);
        System.out.println(filename);

        if (filename != null && filename.exists())
        {
            label1.setText(filename.getName());
            try
            {
                playVideoToPane(pane,filename);
            }
            catch (java.lang.RuntimeException ex)
            {
                label1.setText(label1.getText()+": File not supported");
                label1.setStyle("-fx-text-fill: red");
                System.out.println("File not supported");
            }
        }
    }

    public static void playProcessedVideo(Pane pane,String filePath)
    {
        File file = new File(filePath);
        if (file.exists())
        {
            try
            {
                playVideoToPane(pane,file);
            }
            catch (java.lang.RuntimeException ex)
            {
                System.out.println("ERROR");
            }
        }
        else System.out.println("PROCESSED FILE DOES NOT EXIST");
    }

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
