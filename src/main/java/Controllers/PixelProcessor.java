package Controllers;

import Models.Corner;
import Models.Counter;
import Models.FrameCorners;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Interner;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import net.sf.ehcache.Element;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RRQRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.*;

/**
 * Created by arxa on 16/11/2016.
 */

public class PixelProcessor
{
    private static int cacheId = 0;

    private static List<FrameCorners> frameCornersList;

    private static final int cornerThreshold = 50; // TODO INFO: This number determines if our pixel's value is worth selecting it.

    public static void findHarrisCorners(Mat source)
    {
        Mat gray = new Mat();
        Mat result = new Mat();
        Imgproc.cvtColor(source, gray, Imgproc.COLOR_RGB2GRAY, 0);
        Imgproc.cornerHarris(gray, result, 3, 11, 0.07);
        result.convertTo(result, CvType.CV_8UC3);
        /* System.out.println(result1.type());
           We can use this method to help ourselves how to use convertTo(), cornerHarris e.tc. */

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
                    /*corners[i][j] = pixelValue1.intValue(); // We need this for findPixelsWithNeighbours method
                    findPixelsWithNeighbours(corners,c);*/
                }
            }
        }
        FrameCorners frameCorners = new FrameCorners(source,corners);
        MainController.getCache().put(new Element(cacheId++, frameCorners));
        //frameCornersList.add(new FrameCorners(source,corners));
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

//    public static void printTest()
//    {
//        for (FrameCorners f : frameCornersList)
//        {
//            for (Corner c : f.getStableCorners())
//            {
//                System.out.println(c.getStableCornerDensityProbability());
//            }
//        }
//    }

    public static void applyNormalDistribution(List<Corner> corners)
    {
        double[] xArray = new double[corners.size()];
        double[] yArray = new double[corners.size()];

        /* In order to successfully get the Covariance Matrix, our data need to have a specific structure
           i.e. as many columns as our variables and as many rows as our values for both variables */
        double[][] data = new double[corners.size()][2];
        int i = 0;
        for (Corner c: corners)
        {
            data[i][0] = c.getI();
            data[i][1] = c.getJ();
            xArray[i] = c.getI();
            yArray[i] = c.getJ();
            i++;
        }

        if (data.length < 2){
            return;
        }

        RealMatrix mx = MatrixUtils.createRealMatrix(data);
        RealMatrix cov1 = new Covariance(mx).getCovarianceMatrix();

        // TODO check for Singular is not valid
        // Checking if our Real Matrix is singular
        RRQRDecomposition decomposition = new RRQRDecomposition(cov1);
        DecompositionSolver solver = decomposition.getSolver();
        if (!solver.isNonSingular()){
            return;
        }

        // Calculating means of X and Y
        double[] means = new double[2];

        double sum = 0;
        for (i = 0; i < xArray.length; i++) {
            sum += xArray[i];
        }
        means[0] = sum / xArray.length;

        sum = 0;
        for (i = 0; i < yArray.length; i++) {
            sum += yArray[i];
        }
        means[1] = sum / yArray.length;

        // Applying the Normal Distribution(Bivariate) for two variables: X, Y
        MultivariateNormalDistribution dist = new MultivariateNormalDistribution(means, cov1.getData());

        for (Corner c : corners)
        {
            double[] test = {c.getI(),c.getJ()};
            c.setCornerDensityProbability(dist.density(test));
            // This is the density probability and not the actual probability.
            //System.out.println(dist.density(test));
        }

    }

    public static void findStableAndMovingCorners()
    {
        for (int i=0; i < MainController.getCache().getSize(); i++)
        {
            if (i+1 >= MainController.getCache().getSize()) {
                return;
            }
            for (Corner c1 : getFromCache(i).getCornersList())
            {
                if ( getFromCache(i+1).getCornersList().contains(c1)) // has custom equality
                {
                    getFromCache(i).getStableCorners().add(c1);
                }
                else
                {
                    for (Corner c2 : getFromCache(i+1).getCornersList())
                    {
                        if (c1.getI() == c2.getI())
                        {
                            if (c1.getJ()-c2.getJ() > 0)
                            {
                                c1.getHorDiffList().add(c1.getJ()-c2.getJ());  // Text moves from right to left => positive difference
                                getFromCache(i).getMovingCorners().add(c1);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void findMovingCorners()
    {
        /*Ordering<Counter> byFoundCounter = new Ordering<Counter>() {
            @Override
            public int compare(Counter c1, Counter c2) {
                return Ints.compare(c1.getFoundCounter(), c2.getFoundCounter());
            }
        };*/
        for (Object key : MainController.getCache().getKeys())
        {
            List<Counter> bucket = new ArrayList<>();
            for (Corner c : getFromCache((int)key).getMovingCorners())
            {
                for (int i=0; i < c.getHorDiffList().size(); i++)
                {
                    if (!Counter.findValueAndAddToCounter(bucket,c.getHorDiffList().get(i)))
                    {
                         bucket.add(new Counter(c.getHorDiffList().get(i),1));
                    }
                }
            }
            if (bucket.isEmpty()){
                continue;
            }
            //Integer mostFrequentHorDiff = byFoundCounter.max(bucket).getValue();
            for (Corner c : getFromCache((int)key).getMovingCorners())
            {
                if (Counter.cornerQualifies(bucket,c)){
                    getFromCache((int)key).getQualifiedMovingCorners().add(c);
                }
            }
        }
    }

    public static void cleanCorners(List<Corner> corners)
    {
        Double temp = 0.35*corners.size();
        final Integer percentage = temp.intValue();

        Collections.sort(corners, (c1, c2) -> Double.compare(c1.getCornerDensityProbability(),c2.getCornerDensityProbability()));

        int f = 0;
        Iterator<Corner> i = corners.iterator();
        while (i.hasNext())
        {
            Corner c = i.next();
            if (f < percentage && c.getCornerDensityProbability() > 0.0) {
                i.remove();
                //System.out.println("Item removed");
                f++;
            }
        }
    }

    public static FrameCorners getFromCache(int key) {
        return ((FrameCorners)MainController.getCache().get(key).getObjectValue());
    }

    public static void SortCorners()
    {
        for (Object key : MainController.getCache().getKeys())
        {
            Collections.sort(getFromCache((int)key).getStableCorners(), (c1, c2) -> Integer.compare(c1.getI(),c2.getI()));
            Collections.sort(getFromCache((int)key).getMovingCorners(), (c1, c2) -> Integer.compare(c1.getI(),c2.getI()));
        }
    }

//    public static void cleanStableCorners()
//    {
//        for (FrameCorners f : frameCornersList)
//        {
//            DescriptiveStatistics statsI = new DescriptiveStatistics();
//            //DescriptiveStatistics statsJ = new DescriptiveStatistics();
//
//            List<Integer> bufferI = new ArrayList<>();
//           // List<Integer> bufferJ = new ArrayList<>();
//            List<Integer> diffI = new ArrayList<>();
//            //List<Integer> diffJ = new ArrayList<>();
//            for (Corner c : f.getStableCorners())
//            {
//                bufferI.add(c.getI());
//               // statsJ.addValue(c.getJ());
//            }
//            Collections.sort(bufferI);
//            for (int i=0; i < bufferI.size(); i++)
//            {
//                if (i+1 >= bufferI.size()){
//                    break;
//                }
//                diffI.add(bufferI.get(i+1) - bufferI.get(i));
//            }
//
//            double sum = 0;
//            for (Integer d : diffI) {
//                sum += d;
//            }
//            Double meanI = sum / diffI.size();
//
//            int position=0;
//
//            for (int i=0; i < diffI.size(); i++)
//            {
//                if (diffI.get(i) > 2*meanI){
//                    position = i;
//                    break;
//                }
//            }
//            if (position==0){
//                System.out.println("OK");
//                continue;
//            }
//            List<Integer> temp = new ArrayList<>();
//            for (int i=position+1; i < bufferI.size(); i++)
//            {
//                System.out.println("Deleted: "+bufferI.get(i));
//                temp.add(bufferI.get(i));
//
//            }
//
//            Iterator<Corner> i = f.getStableCorners().iterator();
//            while (i.hasNext())
//            {
//                Corner c = i.next();
//                if (temp.contains(c.getI()))
//                {
//                    i.remove();
//                }
//            }
//        }
//    }

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

    public static void initializeList(){
        frameCornersList = new ArrayList<FrameCorners>();
    }
    public static List<FrameCorners> getFrameCornersList() {
        return frameCornersList;
    }

}
