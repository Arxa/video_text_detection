package Controllers;

import Models.CacheID;
import Models.Color;
import Models.Corner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.List;

/**
 * Created by arxa on 22/11/2016.
 */

public class Visualizer
{
    public static int paintCorners(List<Corner> cornerList, Mat frame, double[] color)
    {
        int paintedCorners = 0;
        frame.convertTo(frame, CvType.CV_64FC3);
        for (Corner c : cornerList)
        {
            outerloop: // Painting a 6x6 area around our Pixel with red, therefore creating a red box for each pixel.
            for (int i = c.getI()-3 ; i < c.getI()+3; i++)
            {
                for (int j = c.getJ()-3; j < c.getJ()+3; j++)
                {
                    if ( (i < 0 || i > frame.height()-1) || (j < 0 || j > frame.width()-1) ) {
                        break outerloop;
                    }
                    frame.put(i,j,color);
                    paintedCorners++;
                }
            }
        }
        return paintedCorners;
    }

    public static void paintTextArea(List<Corner> textCornersList, Mat frame, double[] color)
    {
        frame.convertTo(frame,CvType.CV_64FC3);
        if (textCornersList.isEmpty()){
            return;
        }

        // Using Google Guava for finding the max/min of a List<Object>
        Ordering<Corner> byIStableJMax = new Ordering<Corner>() {
            @Override
            public int compare(Corner c1, Corner c2) {
                return Ints.compare(c1.getJ(), c2.getJ());
            }
        };

        int minI = textCornersList.get(0).getI(); // Minimum i of all corners (stableCorners list is sorted)
        int minJ = byIStableJMax.min(textCornersList).getJ(); // Min J of all corners
        int maxJ = byIStableJMax.max(textCornersList).getJ(); // Max J
        int maxI = Iterables.getLast(textCornersList).getI(); // Max I (list is sorted)

        Corner topLeft = new Corner(minI,minJ);
        Corner topRight = new Corner(minI,maxJ);
        Corner bottomRight = new Corner(maxI,maxJ);
        Corner bottomLeft = new Corner(maxI,minJ);

        int i = topLeft.getI();
        for (int j=topLeft.getJ(); j < topRight.getJ(); j++)
        {
            frame.put(i,j,color);
        }

        int j = topRight.getJ();
        for (i=topRight.getI(); i < bottomRight.getI(); i++)
        {
            frame.put(i,j,color);
        }

        i = bottomRight.getI();
        for (j=bottomRight.getJ(); j > bottomLeft.getJ(); j--)
        {
            frame.put(i,j,color);
        }

        j = bottomLeft.getJ();
        for (i=bottomLeft.getI(); i > topLeft.getI(); i--)
        {
            frame.put(i,j,color);
        }
    }

    public static int paintCornersToBinaryImage(List<Corner> corners, Mat binaryBlackImage1)
    {
        int paintedCorners = 0;
        for (Corner c : corners)
        {
            binaryBlackImage1.put(c.getI(),c.getJ(),255.0); // White Color
            paintedCorners++;
        }
        return paintedCorners;
    }

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
                    new Scalar(Color.RED),2);
        }

    }
}
