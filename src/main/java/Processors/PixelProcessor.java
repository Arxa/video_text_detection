package Processors;

import org.jetbrains.annotations.Contract;
import org.opencv.core.Mat;

/**
 * Created by arxa on 16/11/2016.
 */

public class PixelProcessor
{
    /**
     * Filters a given connected component by checking if its area corresponds to a satisfactory Canny edges threshold,
     * where threshold; the number of Canny Edge Pixels corresponding to the connected component's area divided to
     * the total pixels area, should be greater than 0.05
     * @param left connected component's left coordinate
     * @param top connected component's top coordinate
     * @param width connected component's width
     * @param height connected component's height
     * @return True if the connected component is accepted. False otherwise.
     */
    public static boolean textBlockHasEnoughSobelEdges(double left, double top, double width, double height,
                                                       double connectedComponentArea, Mat dilated)
    {
        int cannyPixels = 0;
        for (int i=(int)top; i<(int)top+(int)height; i++)
        {
            for (int j=(int)left; j<(int)left+(int)width; j++)
            {
                // Considering only the connected component's pixels - and not the bounding box's pixels
                if (Double.compare(dilated.get(i,j)[0],255.0) == 0) {
                    // Do we have an edge pixel in the corresponding Canny image? (Canny edge pixels have 255 color value)
                    if (Double.compare(VideoProcessor.getCanny().get(i,j)[0],255.0) == 0){
                        cannyPixels++;
                    }
                }
            }
        }
        // Are there enough canny edge pixels corresponding in the connected components area?
        return Double.compare(cannyPixels / connectedComponentArea, 0.05) > 0;
    }


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
    @Contract(pure = true)
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
