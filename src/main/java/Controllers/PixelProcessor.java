package Controllers;

import org.opencv.core.Mat;

/**
 * Created by arxa on 16/11/2016.
 */

public class PixelProcessor
{
    public static boolean areaIsSobelDense(double left, double top, double width, double height, double area)
    {
        int cannyPixels = 0;
        for (int i=(int)top; i<(int)top+(int)height; i++)
        {
            for (int j=(int)left; j<(int)left+(int)width; j++)
            {
                if (Double.compare(VideoProcessor.getSobel().get(i,j)[0],255.0) == 0){
                    cannyPixels++;
                }
            }
        }
        return Double.compare(cannyPixels / area, 0.05) > 0;
    }

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
}
