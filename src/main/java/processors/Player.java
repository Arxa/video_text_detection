package processors;

import entities.ApplicationPaths;
import entities.Controllers;
import entities.OutputFolderNames;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;
import java.nio.file.Paths;

public class Player {

    private static MediaPlayer mediaPlayer;

    /**
     * Plays the video file in the GUI pane
     * @param videoFile The file to be played
     * @return True if success, False otherwise
     */
    public static void playVideo(File videoFile) throws Exception
    {
        stopMediaPlayer();
        if (!videoFile.exists()) return;
        String uri = videoFile.toURI().toString();
        Media media = new Media(uri);
        mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);
        Controllers.getMainController().videoPane.getChildren().add(mediaView);
        mediaView.fitHeightProperty().bind(Controllers.getMainController().videoPane.heightProperty());
        mediaView.fitWidthProperty().bind(Controllers.getMainController().videoPane.widthProperty());
        mediaPlayer.play();
    }

    public static void playProcessedVideo() throws Exception {
        File videoFile = Paths.get(ApplicationPaths.RESOURCES_OUTPUTS, ApplicationPaths.UNIQUE_OUTPUT_FOLDER_NAME,
                OutputFolderNames.video.name(), "video.mp4").toFile();
        if (videoFile.exists() && videoFile.isFile()){
            Player.playVideo(videoFile);
        }
    }

    public static void stopMediaPlayer(){
        if (mediaPlayer != null && mediaPlayer.getMedia() != null){
            mediaPlayer.stop();
        }
    }
}
