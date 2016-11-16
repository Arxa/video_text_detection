package Controllers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;


/**
 * Created by arxa on 16/11/2016.
 */
public class HarrisCorner
{
    public static void startAlgorithm(Mat source1, Mat source2, Pane pane, Pane pane1)
    {
        Mat gray1 = new Mat();
        Mat gray2 = new Mat();
        Mat result1 = new Mat();
        Mat result2 = new Mat();

        Imgproc.cvtColor(source1, gray1, Imgproc.COLOR_RGB2GRAY);
        Imgproc.cornerHarris(gray1, result1, 3, 11, 0.07);

        // Converting Mat object to Image
        MatOfByte byteMat1 = new MatOfByte();
        Imgcodecs.imencode(".bmp", result1, byteMat1);
        Image image1 = new Image(new ByteArrayInputStream(byteMat1.toArray()));

        ImageView iv1 = new ImageView(image1);
        pane.getChildren().add(iv1);
        iv1.fitHeightProperty().bind(pane.heightProperty());
        iv1.fitWidthProperty().bind(pane.widthProperty());
    }
}
