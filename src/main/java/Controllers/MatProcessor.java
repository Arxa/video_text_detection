package Controllers;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by arxa on 2/4/2017.
 */

public class MatProcessor
{
    public static List<Mat> getTextBlocksAsMat(List<Rect> textBlocks)
    {
        List<Mat> textRegions = new ArrayList<>();
        for (Rect r : textBlocks) {
            // Creating a crop of the original gray image according to textBlocks rectangles
            Mat crop = new Mat(VideoProcessor.getInput(),r);
            textRegions.add(crop);
        }
        return textRegions;
    }

    public static List<Rect> find_TextBlocks(Mat dilated)
    {
        List<Rect> textBlocks = new ArrayList<>();
        Mat labels = new Mat();
        Mat stats = new Mat();
        Mat centroids = new Mat();
        int numberOfLabels = Imgproc.connectedComponentsWithStats(dilated,labels,stats,centroids,8, CvType.CV_32S);

        // Label 0 is considered to be the background label
        for (int i=1; i<numberOfLabels; i++)
        {
            //stats: (left,top,width,height,area) We care only for the last 3 values
            if ( Double.compare(stats.get(i,2)[0]/stats.get(i,3)[0],1.5) > 0)
            {
                if (PixelProcessor.areaIsSobelDense(stats.get(i,0)[0],stats.get(i,1)[0],
                        stats.get(i,2)[0],stats.get(i,3)[0],stats.get(i,4)[0]))
                {
                    textBlocks.add(new Rect(new Point(stats.get(i,0)[0],stats.get(i,1)[0]),new Size(stats.get(i,2)[0],
                            stats.get(i,3)[0])));
                }
            }
        }
        return textBlocks;
    }

    public static void paintRectsToMat(List<Rect> textBlocks, Mat original)
    {
        for (Rect r : textBlocks)
        {
            // TODO MAYBE THE X AND Y SHOULD BE OPPOSITE??
            Imgproc.rectangle(original, new Point(r.x,r.y), new Point(r.x+r.width,r.y+r.height),
                    new Scalar(255.0),2);
        }

    }
}
