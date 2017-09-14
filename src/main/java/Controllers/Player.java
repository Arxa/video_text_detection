package Controllers;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;

public class Player {

    public static boolean playVideo(File videoFile)
    {
        try {
            String uri = videoFile.toURI().toString();
            Media media = new Media(uri);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);
            References.getMainController().videoPane.getChildren().add(mediaView);
            mediaView.fitHeightProperty().bind(References.getMainController().videoPane.heightProperty());
            mediaView.fitWidthProperty().bind(References.getMainController().videoPane.widthProperty());
            mediaPlayer.play();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
