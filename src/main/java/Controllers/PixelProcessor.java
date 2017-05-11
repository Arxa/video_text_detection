package Controllers;

import Models.Region;
import Models.SubRegion;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;

/**
 * Created by arxa on 16/11/2016.
 */

public class PixelProcessor
{

    public static double[][] find_MaximumGradientDifference(double[][] matArray,int matHeight,int matWidth)
    {
        double[][] mgdArray = new double[matHeight][matWidth];
        for (int i=0; i < matHeight; i++)
        {
            for (int j=10; j < matWidth-10; j++)
            {
                mgdArray[i][j] = get_mgdNumber(i,j,matArray);
            }
        }
        return mgdArray;
    }

    public static double get_mgdNumber(int I, int J, double[][] matArray)
    {
        double min = matArray[I][J-10];
        double max = matArray[I][J-10];
        for (int j =J-9; j <= J+10; j++)
        {
            if (matArray[I][j] > max){
                max = matArray[I][j];
            }
            if (matArray[I][j] < min){
                min = matArray[I][j];
            }
        }
        return max-min;
    }

    public static Mat arrayToMat(double[][] array,int height, int width, int matType)
    {
        Mat image = new Mat(height,width,matType);
        for (int i=0; i<height; i++)
        {
            for (int j=0; j<width; j++)
            {
                image.put(i,j,array[i][j]);
            }
        }
        return image;
    }

    public static double[][] matToArray(Mat frame)
    {
        double array[][] = new double[frame.height()][frame.width()];
        for (int i=0; i < frame.height(); i++)
        {
            for (int j=0; j < frame.width(); j++)
            {
                array[i][j] = frame.get(i,j)[0];
            }
        }
        return array;
    }

    public static double[][] matTo1dArray(Mat frame)
    {
        double array[][] = new double[frame.height()][1];
        for (int i=0; i < frame.height(); i++) {
            array[i][0] = frame.get(i,0)[0];
        }
        return array;
    }

    public static void crop_region_into_subregions(Region textRegions)
    {
        // Cropping the 500x100 text region into 500 10x10 subregions
        for (int x = 0; x <= textRegions.getMatRegion().width() -10; x+=10)
        {
            for (int y = 0; y <= textRegions.getMatRegion().height() -10; y+=10)
            {
                Rect rect = new Rect(new Point(x,y), new Size(10.0,10.0));
                Mat subregion = new Mat(textRegions.getMatRegion(),rect);
                SubRegion subRegion = new SubRegion(subregion);
                textRegions.getSubregions().add(subRegion);
            }
        }
    }

}
