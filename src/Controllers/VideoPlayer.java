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
public class VideoPlayer
{
    private static Stage stage;
    private static FileChooser fileChooser = new FileChooser();
    private static File filename;

    public static void loadLocalVideo(Label label1, Pane pane)
    {
        fileChooser.setTitle("Choose video file");
        filename = fileChooser.showOpenDialog(stage);

        if (filename != null && filename.exists())
        {
            label1.setText(filename.getName());
            String uri = filename.toURI().toString();

            try
            {
                Media media = new Media(uri);
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                MediaView mediaView = new MediaView(mediaPlayer);
                pane.getChildren().add(mediaView);
                mediaView.fitHeightProperty().bind(pane.heightProperty());
                mediaView.fitWidthProperty().bind(pane.widthProperty());
                mediaPlayer.play();
            }
            catch (java.lang.RuntimeException ex)
            {
                label1.setText(label1.getText()+": File not supported");
                label1.setStyle("-fx-text-fill: red");
                System.out.println("File not supported");
            }
        }
    }


    public static File getFilename() {
        return filename;
    }

}
