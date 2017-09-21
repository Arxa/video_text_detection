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
            if ( Double.compare(stats.get(i,2)[0]/stats.get(i,3)[0],2.0) > 0 &&
                    Double.compare(stats.get(i,4)[0],(dilated.height()*dilated.width())*0.004 ) > 0)
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

    public static Mat k_Means(Mat image) {
        Mat data = new Mat(image.height() * image.width(), 1, CvType.CV_32FC1);
        int k = 0;
        for (int i = 0; i < image.height(); i++) {
            for (int j = 0; j < image.width(); j++) {
                data.put(k, 0, image.get(i, j)[0]);
                k++;
            }
        }
        int clusters = 2;
        Mat labels = new Mat();

     /*
         TermCriteria Constructor Parameters:
         type: The type of termination criteria, one of TermCriteria::Type
         maxCount: The maximum number of iterations or elements to compute.
         epsilon: The desired accuracy or change in parameters at which the iterative algorithm stops.
      */
        TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, 10, 1);
        int attempts = 5;
        int flag = Core.KMEANS_PP_CENTERS;
        Mat centers = new Mat();

        Core.kmeans(data, clusters, labels, criteria, attempts, flag, centers);
        return labels;
    }

    public static void paintMatToBinary(Mat labels, Mat binary)
    {
        int k=0;
        for (int i=0; i<binary.height(); i++)
        {
            for (int j=0; j<binary.width(); j++)
            {
                if (Double.compare(labels.get(k,0)[0],1.0) == 0) {
                    binary.put(i,j,255.0);
                } else {
                    binary.put(i,j,0.0);
                }
                k++;
            }
        }
    }
}
