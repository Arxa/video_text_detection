package Controllers;

import Models.CacheID;
import Models.Corner;
import Models.Counter;
import Models.FrameCorners;
import net.sf.ehcache.Element;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RRQRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import java.util.*;

/**
 * Created by arxa on 16/11/2016.
 */

public class PixelProcessor
{
    private static int[][] cornersArray;
    private static Mat result;
    private static final int cornerThreshold = 50; // TODO INFO: This number determines if our pixel's value is worth selecting it.

    public static void findHarrisCorners(Mat source, int cacheId)
    {
        Mat gray = new Mat();
        result = new Mat();
        Imgproc.cvtColor(source, gray, Imgproc.COLOR_RGB2GRAY, 0);
        Imgproc.cornerHarris(gray, result, 3, 11, 0.07);
        result.convertTo(result, CvType.CV_8UC3);

        List<Corner> corners = new ArrayList<>();
        cornersArray = new int[result.height()][result.width()];

        for (int i = 0; i < result.height(); i+=5) // TODO INFO: Scanning step: 5, for speed purposes
        {
            for (int j = 0; j < result.width(); j+=5)
            {
                if (result.get(i,j)[0] > cornerThreshold)  // Corner of the two frames match, according to our cornerThreshold.
                {
                    Double pixelValue1 = result.get(i,j)[0]; // Converting double to Double for using the intValue().
                    Corner c = new Corner(i,j,pixelValue1.intValue());
                    corners.add(c);
                    cornersArray[i][j] = pixelValue1.intValue(); // We need this array for findPixelsWithNeighbours method
                    //findPixelsWithNeighbours(corners,c);
                }
            }
        }
        FrameCorners frameCorners = new FrameCorners(source,corners);
        SystemController.getCache().put(new Element(cacheId, frameCorners));
        // getCache TODO why is this method in this class?
    }


    //TODO FIX THIS
    public static void deleteLonelyCorners()
    {
        for (Object key : SystemController.getCache().getKeys())
        {
            here:
            for (Corner c : SystemController.getFromCache((int)key).getCornersList())
            {
                int count = 0;
                for (int x = c.getI()-25; x < c.getI()+25; x++)
                {
                    for (int y = c.getJ()-25; y < c.getJ()+25; y++)
                    {
                        if ( (x < 0 || x > result.height()-1) || (y < 0 || y > result.width()-1) ) {
                            continue here;
                        }
                        if (cornersArray[x][y] != 0) {
                            count++;
                        }
                    }
                }
                if (count == 0){
                    c.setCornerIsLonely(true);
                }
            }
        }

        for (Object key : SystemController.getCache().getKeys())
        {
            Iterator<Corner> i = SystemController.getFromCache((int)key).getCornersList().iterator();
            while (i.hasNext())
            {
                Corner c = i.next();
                if (c.getCornerIsLonely()) {
                    i.remove();
                }
            }
        }
    }


    public static void testMethod() // removes corners with stable text which means it's not working well
    {
        for (Object key : SystemController.getCache().getKeys())
        {
            for (Corner c1 : SystemController.getFromCache((int)key).getStableCorners())
            {
                for (Corner c2 : SystemController.getFromCache((int)key).getMovingCorners())
                {
                        if (Math.abs(c1.getI()-c2.getI()) < result.height()*0.02 &&
                                Math.abs(c1.getJ()-c2.getJ()) < result.width()*0.02)
                        {
                            c1.setStableCornerIsLonely(true);
                        }
                }
            }
        }

        for (Object key : SystemController.getCache().getKeys())
        {
            Iterator<Corner> i = SystemController.getFromCache((int)key).getStableCorners().iterator();
            while (i.hasNext())
            {
                Corner c = i.next();
                if (c.getStableCornerIsLonely()) {
                    SystemController.getFromCache((int)key).getQualifiedMovingCorners().add(c);
                    i.remove();
                    System.out.println("FILTER: Stable Corner Removed");
                }
            }
        }
    }

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
        for (Corner c1 : SystemController.getFromCache(CacheID.FIRST_FRAME).getCornersList())
        {
            if (SystemController.getFromCache(CacheID.SECOND_FRAME).getCornersList().contains(c1)) // custom equality in contains method
            {
                SystemController.getFromCache(CacheID.FIRST_FRAME).getStableCorners().add(c1);
            }
            else
            {
                for (Corner c2 : SystemController.getFromCache(CacheID.SECOND_FRAME).getCornersList())
                {
                    if (c1.getI() == c2.getI())
                    {
                        if (c1.getJ()-c2.getJ() > 0)
                        {
                            c1.getHorDiffList().add(c1.getJ()-c2.getJ());  // Text moves from right to left => positive difference
                            SystemController.getFromCache(CacheID.FIRST_FRAME).getMovingCorners().add(c1);
                        }
                    }
                }
            }
        }
    }

    // TODO is this the same as deleting the lonely corners?
    /*public static void filterStableCorners()
    {
        int threshold = 20;
        for (Object key : MainController.getCache().getKeys())
        {
            for (int i=1; i < getFromCache((int)key).getStableCorners().size()-1; i++)
            {
                if
                      (
                        getFromCache((int)key).getStableCorners().get(i).getI() -
                        getFromCache((int)key).getStableCorners().get(i-1).getI() > threshold
                        &&
                        Math.abs(getFromCache((int)key).getStableCorners().get(i).getJ() -
                        getFromCache((int)key).getStableCorners().get(i-1).getJ()) > threshold
                        &&
                        getFromCache((int)key).getStableCorners().get(i+1).getI() -
                        getFromCache((int)key).getStableCorners().get(i).getI() > threshold
                        &&
                        Math.abs(getFromCache((int)key).getStableCorners().get(i+1).getJ() -
                        getFromCache((int)key).getStableCorners().get(i).getJ()) > threshold
                      )
                {
                    getFromCache((int)key).getStableCorners().get(i).setStableCornerIsLonely(true);
                }
            }
        }
        for (Object key : MainController.getCache().getKeys())
        {
            Iterator<Corner> i = getFromCache((int)key).getStableCorners().iterator();
            while (i.hasNext())
            {
                Corner c = i.next();
                if (c.getStableCornerIsLonely()) {
                    getFromCache((int)key).getMovingCorners().add(c);
                    i.remove();
                    System.out.println("FILTER: Stable Corner Removed");
                }
            }
        }
        for (Object key : MainController.getCache().getKeys())
        {
            Collections.sort(getFromCache((int)key).getMovingCorners(),
                    (c1, c2) -> Integer.compare(c1.getI(),c2.getI()));
        }
    }*/

    public static void findMovingCorners()
    {
        /*Ordering<Counter> byFoundCounter = new Ordering<Counter>() {
            @Override
            public int compare(Counter c1, Counter c2) {
                return Ints.compare(c1.getFoundCounter(), c2.getFoundCounter());
            }
        };*/
        for (Object key : SystemController.getCache().getKeys())
        {
            List<Counter> bucket = new ArrayList<>();
            for (Corner c : SystemController.getFromCache((int)key).getMovingCorners())
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
            for (Corner c : SystemController.getFromCache((int)key).getMovingCorners())
            {
                if (Counter.cornerQualifies(bucket,c)){
                    SystemController.getFromCache((int)key).getQualifiedMovingCorners().add(c);
                }
            }
        }
    }

    public static void cleanCorners(List<Corner> corners)
    {
        Double temp = 0.2*corners.size();
        final Integer percentage = temp.intValue();

        Collections.sort(corners, (c1, c2) ->
                Double.compare(c1.getCornerDensityProbability(),c2.getCornerDensityProbability()));

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

    public static void sortCorners()
    {
        for (Object key : SystemController.getCache().getKeys())
        {
            Collections.sort(SystemController.getFromCache((int)key).getStableCorners(),
                    (c1, c2) -> Integer.compare(c1.getI(),c2.getI()));
            //Collections.sort(getFromCache((int)key).getMovingCorners(),
            // (c1, c2) -> Integer.compare(c1.getI(),c2.getI()));
        }
    }


    /*public static void cleanStableCorners()
    {
        for (FrameCorners f : frameCornersList)
        {
            DescriptiveStatistics statsI = new DescriptiveStatistics();
            //DescriptiveStatistics statsJ = new DescriptiveStatistics();

            List<Integer> bufferI = new ArrayList<>();
           // List<Integer> bufferJ = new ArrayList<>();
            List<Integer> diffI = new ArrayList<>();
            //List<Integer> diffJ = new ArrayList<>();
            for (Corner c : f.getStableCorners())
            {
                bufferI.add(c.getI());
               // statsJ.addValue(c.getJ());
            }
            Collections.sort(bufferI);
            for (int i=0; i < bufferI.size(); i++)
            {
                if (i+1 >= bufferI.size()){
                    break;
                }
                diffI.add(bufferI.get(i+1) - bufferI.get(i));
            }

            double sum = 0;
            for (Integer d : diffI) {
                sum += d;
            }
            Double meanI = sum / diffI.size();

            int position=0;

            for (int i=0; i < diffI.size(); i++)
            {
                if (diffI.get(i) > 2*meanI){
                    position = i;
                    break;
                }
            }
            if (position==0){
                System.out.println("OK");
                continue;
            }
            List<Integer> temp = new ArrayList<>();
            for (int i=position+1; i < bufferI.size(); i++)
            {
                System.out.println("Deleted: "+bufferI.get(i));
                temp.add(bufferI.get(i));

            }

            Iterator<Corner> i = f.getStableCorners().iterator();
            while (i.hasNext())
            {
                Corner c = i.next();
                if (temp.contains(c.getI()))
                {
                    i.remove();
                }
            }
        }
    }*/
}
