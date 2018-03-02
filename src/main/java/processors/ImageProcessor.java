package processors;

import entities.OutputFolderNames;
import svm.SVM;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arxa on 2/4/2017.
 */

public class ImageProcessor
{
    /**
     * Crops areas from the original image which correspond to the given Rect blocks
     * @param textBlocks The Rect text blocks list
     * @return A list of Mat crops
     */
    public static List<Mat> getCroppedTextBlocks(List<Rect> textBlocks)
    {
        List<Mat> textRegions = new ArrayList<>();
        for (Rect r : textBlocks) {
            Mat crop = new Mat(VideoProcessor.getInput(),r);
            textRegions.add(crop);
        }
        return textRegions;
    }

    /**
     * Finds the text block areas by filtering the connected components of the dilated Mat image.
     * First, finds the candidate text blocks and then filters them.
     * @param dilated The dilated Mat image
     * @return A List of Rect representing the finalist text block areas
     */
    public static List<Rect> findTextBlocks(Mat dilated)
    {
        Mat labels = new Mat();
        Mat stats = new Mat();
        Mat centroids = new Mat();
        int numberOfLabels = Imgproc.connectedComponentsWithStats(dilated,labels,stats,centroids,8, CvType.CV_32S);

        List<Rect> textBlocks = new ArrayList<>();

        // Label 0 is considered to be the background label, so we skip it
        for (int i = 1; i < numberOfLabels; i++)
        {
            // stats columns; [0-4] : [left top width height area}
            Rect textBlock = new Rect(new Point(stats.get(i,0)[0],stats.get(i,1)[0]),new Size(stats.get(i,2)[0],
                    stats.get(i,3)[0]));
            Mat crop = new Mat(VideoProcessor.getInput(),textBlock);
            if ( Double.compare(textBlock.width / textBlock.height, 1.0) >= 0) { // FILTER 1
                if (Double.compare(stats.get(i,4)[0],dilated.height() * dilated.width() * 0.002 ) > 0){ // FILTER 2
                    Imgproc.cvtColor(crop, crop, Imgproc.COLOR_RGB2GRAY, 0);
                    Imgproc.resize(crop, crop, new Size(100,50), 4.0, 4.0, Imgproc.INTER_LINEAR);
                    if (SVM.blockContainsText(crop)){ // FILTER 3
                        ImageWriter.writeOutputImage(crop, OutputFolderNames.svm_has_text);
                        textBlocks.add(textBlock);
                    } else ImageWriter.writeOutputImage(crop, OutputFolderNames.filter3_svm_no_text);
                } else ImageWriter.writeOutputImage(crop, OutputFolderNames.filter2_too_small);
            } else ImageWriter.writeOutputImage(crop, OutputFolderNames.filter1_too_high);
        }
        return textBlocks;
    }

    /**
     * Paints with red the text block boundaries of the original image
     * @param textBlocks The list of text blocks
     * @param original Original image
     */
    public static void paintTextBlocks(List<Rect> textBlocks, Mat original)
    {
        for (Rect r : textBlocks)
        {
            Imgproc.rectangle(original, new Point(r.x,r.y), new Point(r.x+r.width,r.y+r.height),
                    new Scalar(255.0),2);
        }
    }

    /**
     * Applies the k-means clustering algorithm, using only 2 clusters,
     * in order to achieve image thresholding
     * @param image The target Mat image
     * @return The original image converted to binary (black and white)
     */
    public static Mat thresholdImageWithKmeans(Mat image)
    {
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
        return ImageProcessor.convertLabelsToBinary(labels, image);
    }

    /**
     * Converts the k-means labels result into a new binary image
     * @param labels The labels result from the kmeans algorithm
     * @param image The Mat image for which we calculated the kmeans thresholding
     * @return The binary image that has been created
     */
    public static Mat convertLabelsToBinary(Mat labels, Mat image)
    {
        Mat binary = new Mat(image.height(), image.width(), CvType.CV_8UC1);
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
        return binary;
    }
}
