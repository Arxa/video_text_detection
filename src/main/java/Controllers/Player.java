package Controllers;

import Models.Corner;
import Models.FrameCorners;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.ehcache.Status;
import org.opencv.core.CvType;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import static Controllers.PixelProcessor.getFromCache;

/**
 * Created by arxa on 16/11/2016.
 */

public class Player
{
    private static Stage stage;
    private static FileChooser fileChooser;
    private static File filename;

    public static void playLocalVideo(Label label1, Pane pane)
    {
        FileChooser fileChooser = new FileChooser();
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

    public static void playFramesWithCorners(Pane pane2)
    {
        Task task = new Task<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                for (int i=0; i < MainController.getCache().getSize(); i++)
                {
                    int finalI = i;
                    Platform.runLater(() -> {
                        Platform.setImplicitExit(false);
                        System.out.println("Showing image");

                        // Converting Mat to Image
                        MatOfByte byteMat1 = new MatOfByte();
                        Imgcodecs.imencode(".bmp", getFromCache(finalI).getFrame(), byteMat1);
                        Image image1 = new Image(new ByteArrayInputStream(byteMat1.toArray()));
                        ImageView iv1 = new ImageView(image1);
                        // Show frame on Scene
                        pane2.getChildren().add(iv1);
                        iv1.fitHeightProperty().bind(pane2.heightProperty());
                        iv1.fitWidthProperty().bind(pane2.widthProperty());
                    });
                    Thread.sleep(2000);
                }
                return null;
            }

            @Override protected void succeeded() {
                if (MainController.getCm().getStatus() == Status.STATUS_ALIVE){
                    MainController.getCm().shutdown();
                }
                super.succeeded();
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }



    public static File getFilename() {
        return filename;
    }

}
