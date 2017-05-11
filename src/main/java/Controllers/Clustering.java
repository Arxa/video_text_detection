package Controllers;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arxa on 11/3/2017.
 */

public class Clustering
{
    /**
     * @see <a href="http://docs.opencv.org/3.1.0/d5/d38/group__core__cluster.html#ga9a34dc06c6ec9460e90860f15bcd2f88">OpenCV-kmeans</a>
     * <a href="http://docs.opencv.org/3.1.0/d9/d5d/classcv_1_1TermCriteria.html">OpenCV-TermCriteria</a>
     */
    public static Mat k_Means(Mat image)
    {
        Mat data = new Mat(image.height()*image.width(),1, CvType.CV_32FC1);
        int k = 0;
        for (int i=0; i<image.height(); i++)
        {
            for (int j=0; j<image.width(); j++)
            {
                data.put(k,0,image.get(i,j)[0]);
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
        TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER,data.height(),1.0 );
        int attempts = 5;
        int flag = Core.KMEANS_PP_CENTERS;
        Mat centers = new Mat();

        Core.kmeans(data,clusters,labels,criteria,attempts,flag,centers);

        /*System.out.println("LABELS");
        System.out.println(labels.height()+" , "+labels.width());
        int ccc = 0;
        outerloop:
        for (int i=0; i<labels.height(); i++)
        {
            for (int j=0; j<labels.width(); j++)
            {
                //System.out.println(labels.get(i,j)[0]);
                ccc++;
                if (ccc > 10000){
                    break outerloop;
                }
            }
        }*/

        /*System.out.println("CENTERS");
        for (int i=0; i<centers.height(); i++)
        {
            for (int j=0; j<centers.width(); j++)
            {
                System.out.println(centers.get(i,j)[0]);
            }
        }*/
        return labels;
    }

    public static List<Rect> find_TextBlocks(Mat dilated)
    {
        List<Rect> textBlocks = new ArrayList<>();
        Mat labels = new Mat();
        Mat stats = new Mat();
        Mat centroids = new Mat();
        int numberOfLabels = Imgproc.connectedComponentsWithStats(dilated,labels,stats,centroids,8,CvType.CV_32S);

        // Label 0 is considered to be the background label
        for (int i=1; i<numberOfLabels; i++)
        {
            //stats: (left,top,width,height,area) We care only for the last 3 values
            if ( Double.compare(stats.get(i,2)[0]/stats.get(i,3)[0],1.5) > 0)
            {
                if (Filtering.blockIsSobelDense(stats.get(i,0)[0],stats.get(i,1)[0],
                        stats.get(i,2)[0],stats.get(i,3)[0],stats.get(i,4)[0]))
                {
                    textBlocks.add(new Rect(new Point(stats.get(i,0)[0],stats.get(i,1)[0]),new Size(stats.get(i,2)[0],
                            stats.get(i,3)[0])));
                }
            }
        }
        return textBlocks;
    }
}
