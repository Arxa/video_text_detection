package Controllers;

import Models.ImageContainer;

/**
 * Created by arxa on 12/3/2017.
 */

public class Filtering
{
    public static boolean blockIsSobelDense(double left, double top, double width, double height, double area)
    {
        double sobelEdgeDensity = 0.0;
        for (int i=(int)top; i<(int)top+(int)height; i++)
        {
            for (int j=(int)left; j<(int)left+(int)width; j++)
            {
                sobelEdgeDensity += ImageContainer.getInput_Sobel().get(i,j)[0];
            }
        }
        return Double.compare(sobelEdgeDensity / area, 2.0) > 0;
    }
}
