package processors;

import org.opencv.core.Mat;

/**
 * Created by arxa on 16/11/2016.
 */

public class PixelProcessor
{
    /**
     * Returns the MaximumGradientDifference operation of the input image
     * by calculating the MGD number of every pixel
     * @param laplaceImage The target Mat image
     * @return The MGD result in double 2D array format
     */
    public static double[][] getMgdArray(Mat laplaceImage)
    {
        double[][] laplaceArray = PixelProcessor.matToArray(laplaceImage);
        double[][] mgdArray = new double[laplaceImage.height()][laplaceImage.width()];
        for (int i=0; i < laplaceImage.height(); i++) {
            for (int j=10; j < laplaceImage.width() - 10; j++) {
                mgdArray[i][j] = getMgdNumber(i,j,laplaceArray);
            }
        }
        return mgdArray;
    }

    /**
     * Calculates the MGD number of a single image pixel
     * @param I The height-coordinate of the given pixel
     * @param J The width-coordinate of the given pixel
     * @param matArray The entire respective image stored in a 2D array
     * @return The MGD number for the pixel that was given
     */
    public static double getMgdNumber(int I, int J, double[][] matArray)
    {
        double min = matArray[I][J-10];
        double max = matArray[I][J-10];
        for (int j=J-9; j <= J+10; j++)
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
