package processors;

import entities.ApplicationPaths;
import entities.Controllers;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;
import java.nio.file.Paths;

public class Player {

    /**
     * Plays the video file in the GUI pane
     * @param videoFile The file to be played
     * @return True if success, False otherwise
     */
    public static void playVideo(File videoFile) throws Exception
    {
        String uri = videoFile.toURI().toString();
        Media media = new Media(uri);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);
        Controllers.getMainController().videoPane.getChildren().add(mediaView);
        mediaView.fitHeightProperty().bind(Controllers.getMainController().videoPane.heightProperty());
        mediaView.fitWidthProperty().bind(Controllers.getMainController().videoPane.widthProperty());
        mediaPlayer.play();
    }

    public static void playProcessedVideo() throws Exception {
        String videoFilePath = Paths.get(ApplicationPaths.RESOURCES_OUTPUTS, ApplicationPaths.UNIQUE_FOLDER_NAME,
                    "Video", "video.mp4").toAbsolutePath().toString();
        File videoFile = new File(videoFilePath);
        if (videoFile.exists() && videoFile.isFile()){
            Player.playVideo(videoFile);
        }
    }
}
