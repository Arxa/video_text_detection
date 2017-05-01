package Controllers;

import Models.Region;
import Models.SubRegion;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Models.ImageContainer;


/**
 * Created by arxa on 2/4/2017.
 */

public class MatProcessor
{
    public static List<Mat> getTextBlockList(List<Rect> textBlocks)
    {
        List<Mat> textRegions = new ArrayList<>();
        for (Rect r : textBlocks)
        {
            // Creating a crop of the original gray image according to textBlocks rectangles
            Mat crop = new Mat(ImageContainer.getInput(),r);
            //Mat resized = new Mat();
            // sz = new Size(250,50); // Adjusting resolution
            //Imgproc.resize(crop, resized, sz);
            //Mat resizedGray = new Mat();
            textRegions.add(crop);
            //ImageContainer.getCroppedTextRegions().add(new Region(resized));
        }
        return textRegions;
    }

    public static Mat getTestSVMImage(Region r)
    {
        Mat testSVMImage = new Mat(Training.SUBREGIONS,Training.FEATURE_DIMENSIONS, CvType.CV_32FC1);
        int column = 0, row = 0;
        PixelProcessor.crop_region_into_subregions(r);
        for (SubRegion s : r.getSubregions())
        {
            URLBP.generateFeatureBaseVector();
            Map<Integer,Integer> histogram = URLBP.calculateURLBP(s.getMatSubregion());
            for (Integer key : histogram.keySet()) {
                testSVMImage.put(row,column++,(double) histogram.get(key));
            }
            column = 0;
            row++;
        }
        return testSVMImage;
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
