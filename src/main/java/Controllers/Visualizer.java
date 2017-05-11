package Controllers;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

/**
 * Created by arxa on 22/11/2016.
 */

public class Visualizer
{
    public static void paintMatToBinary(Mat labels, Mat binary)
    {
        int k=0;
        for (int i=0; i<binary.height(); i++)
        {
            for (int j=0; j<binary.width(); j++)
            {
                if (Double.compare(labels.get(k,0)[0],1.0) == 0)
                {
                    binary.put(i,j,255.0);
                }
                k++;
            }
        }
    }

    public static void paintRectsToMat(List<Rect> textBlocks, Mat original)
    {
        for (Rect r : textBlocks)
        {
            Imgproc.rectangle(original, new Point(r.x,r.y), new Point(r.x+r.width,r.y+r.height),
                    new Scalar(255.0),2);
        }

    }
}
