package Controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.io.File;



public class MainController
{
    public Pane pane;
    public Pane pane2;

    public MainController() {
    }

    private Stage stage;
    private FileChooser fileChooser = new FileChooser();

    public Label label1;


    public void loadVideo(ActionEvent mouseEvent)
    {
        fileChooser.setTitle("Choose video file");
        File filename = fileChooser.showOpenDialog(stage);
        if (filename!=null)
        {
            if (filename.exists())
            {
                label1.setText(filename.getName());
                String uri = filename.toURI().toString();

                try
                {
                    Media media = new Media(uri);
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    MediaView mediaView = new MediaView(mediaPlayer);

                    StackPane root = new StackPane();
                    root.getChildren().add(mediaView);

                    pane.getChildren().add(mediaView);

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





        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

        VideoCapture cap = new VideoCapture();
        cap.open(filename.toString());
        if (cap.isOpened())
        {
            System.out.println("ok");
            Mat frame = new Mat();

            if (cap.read(frame))
            {
                System.out.println("is empty");
            }
            else
            {
                System.out.println("not empty");
            }

            // Converting Mat object to Image
            MatOfByte byteMat = new MatOfByte();
            Imgcodecs.imencode(".bmp", frame, byteMat);
            Image image = new Image(new ByteArrayInputStream(byteMat.toArray()));


            ImageView iv = new ImageView(image);
            pane2.getChildren().add(iv);
        }
        else
        {
            System.out.println("not ok");
        }
    }
}


