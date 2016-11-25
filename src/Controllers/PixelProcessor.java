package Controllers;

import Models.Corner;
import Models.FrameCorners;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arxa on 16/11/2016.
 */

public class PixelProcessor
{
    private static Mat gray1 = new Mat();
    private static Mat gray2 = new Mat();
    private static Mat result1 = new Mat();
    private static Mat result2 = new Mat();
    private static List<FrameCorners> frameCornersList = new ArrayList<>();

    private static final int cornerThreshold = 50; // TODO INFO: This number determines if our pixel's value is worth selecting it.

    public static void findMatchedCorners(Mat source1, Mat source2)
    {
        // --- 1st pair's frame ---
        Imgproc.cvtColor(source1, gray1, Imgproc.COLOR_RGB2GRAY, 0);
        Imgproc.cornerHarris(gray1, result1, 3, 11, 0.07);
        result1.convertTo(result1, CvType.CV_8UC3);
        //System.out.println(result1.type()); //We can use this method to help ourselves how to use convertTo(), cornerHarris e.tc.

        // --- 2nd pair's frame ---
        Imgproc.cvtColor(source2, gray2, Imgproc.COLOR_RGB2GRAY, 0);
        Imgproc.cornerHarris(gray2, result2, 3, 11, 0.07);
        result2.convertTo(result2, CvType.CV_8UC3);

        Integer[][] corners = new Integer[result1.height()][result1.width()];

        List<Corner> matchedCornerList = new ArrayList<>();
        for (int i = 0; i < result1.height(); i+=5) // TODO INFO: Scanning step: 5, for speed purposes
        {
            for (int j = 0; j < result1.width(); j+=5)
            {
                if (result1.get(i,j)[0] > cornerThreshold && result2.get(i,j)[0] > cornerThreshold)  // Corner of the two frames match, according to our cornerThreshold.
                {
                    Double pixelValue1 = result1.get(i,j)[0]; // Converting double to Double for using the intValue().
                    Corner c = new Corner(i,j,pixelValue1.intValue());
                    matchedCornerList.add(c); // TODO INFO: We are placing the first frame's value into our list. Maybe we can change that to the average of both.
                    corners[i][j] = pixelValue1.intValue(); // We need this for findPixelsWithNeighbours method
                    findPixelsWithNeighbours(corners,c);
                }
            }
        }
        frameCornersList.add(new FrameCorners(source1, matchedCornerList));
        frameCornersList.add(new FrameCorners(source2, matchedCornerList));
    }


    public static void findPixelsWithNeighbours(Integer[][] corners, Corner c)
    {
        int count = 0;
        c.setHasPixelsAround(false);
        for (int i = c.getI()-7 ; i < c.getI()+7; i++)
        {
            for (int j = c.getJ()-7; j < c.getJ()+7; j++)
            {
                if ( (i < 0 || i > result1.height()-1) || (j < 0 || j > result1.width()-1) ) {
                    return;
                }

                if (corners[i][j] != null)
                {
                    count++;
                }
            }
        }
        if (count >= 2)
        {
            c.setHasPixelsAround(true);
        }
    }

    public static List<FrameCorners> getFrameCornersList() {
        return frameCornersList;
    }
}
