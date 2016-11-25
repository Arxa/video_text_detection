package Controllers;

import Models.Corner;
import Models.FrameCorners;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by arxa on 22/11/2016.
 */

public class Visualizer
{
    public static void paintCorners(List<FrameCorners> frameCornersList)
    {
        double[] data = {0.0, 0.0, 230.0}; // RED

        for (FrameCorners f : frameCornersList)
        {
            f.getFrame().convertTo(f.getFrame(), CvType.CV_64FC3);
            for (Corner c : f.getCornersList())
            {
                outerloop: // Painting a 6x6 area around our Pixel with red, therefore creating a red box for each pixel.
                for (int i = c.getI()-3 ; i < c.getI()+3; i++)
                {
                    for (int j = c.getJ()-3; j < c.getJ()+3; j++)
                    {
                        if ( (i < 0 || i > f.getFrame().height()-1) || (j < 0 || j > f.getFrame().width()-1) ) {
                            break outerloop;
                        }
                        f.getFrame().put(i,j,data);
                    }
                }
            }
        }
    }
}
