package Controllers;

import com.sun.xml.internal.bind.v2.TODO;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;

/**
 * Created by arxa on 16/11/2016.
 */

public class FrameProcessor
{
    private static VideoCapture cap;

    public static void getInitialFrames(Pane pane2, Pane pane3)
    {
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

        cap = new VideoCapture();
        cap.open(VideoPlayer.getFilename().toString());
        if (cap.isOpened())
        {
            System.out.println("ok");
            Mat frame1 = new Mat();
            Mat frame2 = new Mat();

            if (cap.read(frame1) && cap.read(frame2))
            {
                // Converting Mat object to Image
                MatOfByte byteMat1 = new MatOfByte();
                Imgcodecs.imencode(".bmp", frame1, byteMat1);
                Image image1 = new Image(new ByteArrayInputStream(byteMat1.toArray()));

                ImageView iv1 = new ImageView(image1);
                pane2.getChildren().add(iv1);
                iv1.fitHeightProperty().bind(pane2.heightProperty());
                iv1.fitWidthProperty().bind(pane2.widthProperty());

                // Converting Mat object to Image
                MatOfByte byteMat2 = new MatOfByte();
                Imgcodecs.imencode(".bmp", frame2, byteMat2);
                Image image2 = new Image(new ByteArrayInputStream(byteMat2.toArray()));

                ImageView iv2 = new ImageView(image2);
                pane3.getChildren().add(iv2);
                iv2.fitHeightProperty().bind(pane3.heightProperty());
                iv2.fitWidthProperty().bind(pane3.widthProperty());

                //HarrisCorner.startAlgorithm(frame1, frame2, pane2, pane3);
            }
            else
            {
                System.out.println("not empty");
            }
        }
        else
        {
            System.out.println("not ok");
        }
    }

    // TODO Handle the Exceptions
    public static void grabFrames()
    {
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        Mat frame1 = new Mat();
        Mat frame2 = new Mat();
        if (cap != null)
        {
            while (true)
            {
                if (cap.read(frame1) && cap.read(frame2))
                {
                    HarrisCorner.startAlgorithm(frame1, frame2);
                }
                else
                {
                    break;
                }
            }
        }
    }


    public static VideoCapture getCap() {
        return cap;
    }
}
