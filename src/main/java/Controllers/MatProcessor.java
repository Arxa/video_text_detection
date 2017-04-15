package Controllers;

import Models.Region;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.List;
import Models.ImageContainer;


/**
 * Created by arxa on 2/4/2017.
 */

public class MatProcessor
{
    public static void crop_mat_into_text_regions(List<Rect> textBlocks)
    {
        for (Rect r : textBlocks)
        {
            // Creating a crop of the original gray image according to textBlocks rectangles
            Mat crop = new Mat(ImageContainer.getInput(),r);
            Mat resized = new Mat();
            Mat resized_gray = new Mat();
            Size sz = new Size(500,100); // Adjusting resolution
            Imgproc.resize(crop, resized, sz);
            Imgproc.cvtColor(resized, resized_gray, Imgproc.COLOR_RGB2GRAY, 0);
            ImageContainer.getCroppedTextRegions().add(new Region(resized_gray));
            //Writer.writeFrameAsImage(resized_gray);
        }
    }

    public static Mat change_mat_resolution(Mat input, int width, int height)
    {
        Mat resized = new Mat();
        Size sz = new Size(width,height);
        Imgproc.resize(input, resized, sz);
        return resized;
    }

    public static void printMat(Mat m)
    {
        for (int x=0; x < m.height(); x++)
        {
            System.out.printf("\n");
            System.out.printf("ROW: %d",x);
            System.out.printf("%s"," -- ");
            for (int y=0; y < m.width(); y++)
            {
                System.out.printf("%f",m.get(x,y)[0]);
                System.out.printf("%s"," ");
            }
        }
    }


}
