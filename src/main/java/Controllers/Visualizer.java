package Controllers;

import Models.Corner;
import Models.FrameCorners;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.List;

/**
 * Created by arxa on 22/11/2016.
 */

public class Visualizer
{
    public static void paintCorners(List<Corner> cornerList, Mat frame, String type)
    {
        double[] data  = new double[3];
        if (type.equals("stable")){ // RED
            data[0] = 0.0;
            data[1] = 0.0;
            data[2] = 255.0;
        }
        else{ // BLUE
            data[0] = 255.0;
            data[1] = 128.0;
            data[2] = 0.0;
        }

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
                    frame.put(i,j,data);
                }
            }
        }
    }

    public static void paintTextArea(List<Corner> textCornersList, Mat frame)
    {
        double[] data = {0.0, 255.0, 0.0}; // GREEN
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
            frame.put(i,j,data);
        }

        int j = topRight.getJ();
        for (i=topRight.getI(); i < bottomRight.getI(); i++)
        {
            frame.put(i,j,data);
        }

        i = bottomRight.getI();
        for (j=bottomRight.getJ(); j > bottomLeft.getJ(); j--)
        {
            frame.put(i,j,data);
        }

        j = bottomLeft.getJ();
        for (i=bottomLeft.getI(); i > topLeft.getI(); i--)
        {
            frame.put(i,j,data);
        }
    }
}
