package Controllers;

import Models.Corner;
import Models.FrameCorners;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Interner;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.*;

/**
 * Created by arxa on 16/11/2016.
 */

public class PixelProcessor
{
    private static Mat gray = new Mat();
    private static Mat result = new Mat();

    private static List<FrameCorners> frameCornersList = new ArrayList<>();

    private static final int cornerThreshold = 50; // TODO INFO: This number determines if our pixel's value is worth selecting it.

    public static void findMatchedCorners(Mat source)
    {
        Imgproc.cvtColor(source, gray, Imgproc.COLOR_RGB2GRAY, 0);
        Imgproc.cornerHarris(gray, result, 3, 11, 0.07);
        result.convertTo(result, CvType.CV_8UC3);
        //System.out.println(result1.type()); //We can use this method to help ourselves how to use convertTo(), cornerHarris e.tc.

        //Integer[][] corners = new Integer[result1.height()][result1.width()];

        List<Corner> corners = new ArrayList<>();

        for (int i = 0; i < result.height(); i+=5) // TODO INFO: Scanning step: 5, for speed purposes
        {
            for (int j = 0; j < result.width(); j+=5)
            {
                if (result.get(i,j)[0] > cornerThreshold)  // Corner of the two frames match, according to our cornerThreshold.
                {
                    Double pixelValue1 = result.get(i,j)[0]; // Converting double to Double for using the intValue().
                    Corner c = new Corner(i,j,pixelValue1.intValue());
                    corners.add(c);
//                    corners[i][j] = pixelValue1.intValue(); // We need this for findPixelsWithNeighbours method
//                    findPixelsWithNeighbours(corners,c);
                }
            }
        }
        frameCornersList.add(new FrameCorners(source,corners));
    }


//    public static void findPixelsWithNeighbours(Integer[][] corners, Corner c)
//    {
//        int count = 0;
//        c.setHasPixelsAround(false);
//        for (int i = c.getI()-7 ; i < c.getI()+7; i++)
//        {
//            for (int j = c.getJ()-7; j < c.getJ()+7; j++)
//            {
//                if ( (i < 0 || i > result1.height()-1) || (j < 0 || j > result1.width()-1) ) {
//                    return;
//                }
//
//                if (corners[i][j] != null)
//                {
//                    count++;
//                }
//            }
//        }
//        if (count >= 2)
//        {
//            c.setHasPixelsAround(true);
//        }
//    }

    public static void findStableAndMovingCorners()
    {
        for (int i=0; i < frameCornersList.size(); i++)
        {
            if (i+1 >= frameCornersList.size()) {

                return;
            }
            for (Corner c1 : frameCornersList.get(i).getCornersList())
            {
                for (Corner c2 : frameCornersList.get(i+1).getCornersList())
                {
                    if (c1.getI().equals(c2.getI()))
                    {
                        if (c1.getJ()-c2.getJ() == 0)
                        {
                            frameCornersList.get(i).getStableCorners().add(c1);
                            continue;
                        }
                        else if (c1.getJ()-c2.getJ() > 0)
                        {
                            c1.getHorDiffList().add(c1.getJ()-c2.getJ());  // Text moves from right to left => positive difference
                            frameCornersList.get(i).getMovingCorners().add(c1);
                        }
                    }
                    if (c1.getJ().equals(c2.getJ()))
                    {
                        if (c1.getI()-c2.getI() == 0)
                        {
                            frameCornersList.get(i).getStableCorners().add(c1);
                        }
                        else
                        {
                            c1.getVerDiffList().add(c1.getI()-c2.getI()); // Text moves from bottom to top
                            frameCornersList.get(i).getMovingCorners().add(c1);
                        }
                    }
                }
            }
        }
    }

    public static void findMovingCorners()
    {
        boolean qualifier;
        for (int i=0; i < frameCornersList.size()-1; i++)
        {
            if (i+1 >= frameCornersList.size()-1){
                return;
            }
            here:
            for (Corner c1 : frameCornersList.get(i).getMovingCorners())
            {
                qualifier = false;
                for (Integer d1 : c1.getHorDiffList())
                {
                    for (Corner c2 : frameCornersList.get(i+1).getMovingCorners())
                    {
                        if (c2.getI() < c1.getI()){
                            continue;
                        }
                        if (c2.getI() > c1.getI()){
                            continue here;
                        }
                        for (Integer d2 : c2.getHorDiffList())
                        {
                            if (d1.equals(d2))
                            {
                                qualifier = true;
                                frameCornersList.get(i+1).getQualifiedMovingCorners().add(new Corner(c1.getI(),c1.getJ()+d1));
                            }
                        }
                    }
                }
                if (qualifier){
                    frameCornersList.get(0).getQualifiedMovingCorners().add(c1);
                }
            }
        }
    }

    public static void SortCorners()
    {
        for (FrameCorners f : frameCornersList)
        {
            Collections.sort(f.getStableCorners(), (c1, c2) -> c1.getI().compareTo(c2.getI()));
            Collections.sort(f.getMovingCorners(), (c1, c2) -> c1.getI().compareTo(c2.getI()));
        }
    }

//    public static void printResults()
//    {
//        int i=0;
//        for (FrameCorners f : frameCornersList)
//        {
//            System.out.println("Frame "+i+" -> "+(i+1));
//            System.out.println(" ------------------------------");
//            System.out.println("Corners with same X: ");
//            for (Corner c : f.getCornersList())
//            {
//                for (Integer d : c.getHorDiffList())
//                {
//                    if (d==0)
//                    {
//                        System.out.println(c.toString()+"----- "+d);
//                    }
//                }
//            }
//            System.out.println("Corners with same Y: ");
//            for (Corner c : f.getCornersList())
//            {
//                for (Integer d : c.getVerDiffList())
//                {
//                    if (d==0)
//                    {
//                        System.out.println(c.toString()+"----- "+d);
//                    }
//                }
//            }
//            System.out.println("\n\n");
//            i++;
//            //if (i>3) break;
//        }
//    }


    public static List<FrameCorners> getFrameCornersList() {
        return frameCornersList;
    }
}
