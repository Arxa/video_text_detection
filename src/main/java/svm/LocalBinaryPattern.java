package svm;

import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntIntHashMap;
import libsvm.svm;
import org.opencv.core.Mat;

public class LocalBinaryPattern {

    // Using this number to store all non-uniform patter frequencies
    private static final int NON_UNIFORM_BIN = 999;

    // 59 dimensions feature vector, based on the LocalBinaryPattern histogram
    private static OpenIntIntHashMap histogramTemplate = getHistogramTemplate();

    /**
     * Extracts features from a Mat image based on the LocalBinaryPattern (Uniform Rotated Local Binary Pattern) as
     * described in the following <a href="https://www.cs.tut.fi/~mehta/rlbp">paper/a>.
     * @param image The Mat image
     * @return A 59-length feature vector - the value set of the histogram
     */
    public static int[] get_URLBP_Features(Mat image)
    {
        // Using: R(Radius) = 1, P(Pixel Neighbours) = 8
        OpenIntIntHashMap histogram = (OpenIntIntHashMap) histogramTemplate.clone();

        for (int x=1; x < image.rows() - 1; x++)
        {
            for (int y=1; y < image.cols() - 1; y++)
            {
                double[] diffs = new double[8];
                // TODO try with >= !!!!!!!
                // Find the differences between the current center pixel and its neighbours
                double center = image.get(x,y)[0]; // Starting from the same row, right column pixel, clockwise
                diffs[0] = (Double.compare(image.get(x,y+1)[0], center) > 0) ? image.get(x,y+1)[0] - center : 0.0;
                diffs[1] = (Double.compare(image.get(x+1,y+1)[0],center) > 0) ? image.get(x+1,y+1)[0] - center : 0.0;
                diffs[2] = (Double.compare(image.get(x+1,y)[0],center) > 0) ? image.get(x+1,y)[0] - center : 0.0;
                diffs[3] = (Double.compare(image.get(x+1,y-1)[0],center) > 0) ? image.get(x+1,y-1)[0] - center : 0.0;
                diffs[4] = (Double.compare(image.get(x,y-1)[0],center) > 0) ? image.get(x,y-1)[0] - center : 0.0;
                diffs[5] = (Double.compare(image.get(x-1,y-1)[0],center) > 0) ? image.get(x-1,y-1)[0] - center : 0.0;
                diffs[6] = (Double.compare(image.get(x-1,y)[0],center) > 0) ? image.get(x-1,y)[0] - center : 0.0;
                diffs[7] = (Double.compare(image.get(x-1,y+1)[0],center) > 0) ? image.get(x-1,y+1)[0] - center : 0.0;

                // Finding the dominant direction pixel i.e. the pixel with the biggest difference
                double max = Double.MIN_VALUE;
                int dominantDirection = -1;
                for (int i=0; i < diffs.length; i++)
                {
                    if (Double.compare(diffs[i],0.0) > 0){
                        if (Double.compare(diffs[i],max) > 0){
                            max = diffs[i];
                            dominantDirection = i;
                        }
                    }
                }

                // if none of pixel neighbours have a greater value, the result is 0
                if (dominantDirection == -1) {
                    histogram.put(0,histogram.get(0)+1);
                    continue;
                }

                // Calculating the binary pattern
                int binary_pattern = 0;
                int power = 1;
                for (int i=dominantDirection; i < diffs.length; i++)
                {
                    if (Double.compare(diffs[i],0.0) > 0) {
                        binary_pattern += power;
                    }
                    power *= 2;
                }

                for (int i=0; i < dominantDirection; i++)
                {
                    if (Double.compare(diffs[i],0.0) > 0){
                        binary_pattern += power;
                    }
                    power *= 2;
                }

                // If binary pattern is uniform increase is hist value otherwise increase the NON_UNIFORM_BIN
                if (isBinaryUniform(convertToBinary(binary_pattern))){ // Key already exists, so increment its value
                    histogram.put(binary_pattern,histogram.get(binary_pattern)+1);
                }
                else {
                    histogram.put(NON_UNIFORM_BIN,histogram.get(NON_UNIFORM_BIN)+1);
                }
            }
        }
        IntArrayList keyList = new IntArrayList();
        IntArrayList valueList = new IntArrayList();
        histogram.pairsSortedByKey(keyList,valueList);
        return valueList.elements(); // returning the 59 freq-values of the histogram
    }

    private static boolean isBinaryUniform(int[] binary)
    {
        int transitions = 0;
        for (int i=0; i < binary.length - 1; i++) {
            if (binary[i] != binary[i+1]) {
                transitions++;
            }
        }
        return transitions <= 2;
    }

    private static int[] convertToBinary(int number)
    {
        int[] binary = new int[8];
        for (int i = 7, num = number; i >= 0; i--, num >>>= 1)
            binary[i] = num & 1;
        return binary;
    }

    private static OpenIntIntHashMap getHistogramTemplate() // Should be called only one time
    {
        OpenIntIntHashMap histogram = new OpenIntIntHashMap();
        histogram.put(NON_UNIFORM_BIN,0); // Single bin for non-uniform patterns
        for (int i=0; i <= 255; i++) // Placing bins for uniform patterns separately. Map size should be 59
        {
            if (isBinaryUniform(convertToBinary(i))){
                histogram.put(i,0);
            }
        }
        return histogram;
    }

    public static int[] get_ULBP_Features(Mat image)
    {
        // Using: R(Radius) = 1, P(Pixel Neighbours) = 8
        OpenIntIntHashMap histogram = (OpenIntIntHashMap) histogramTemplate.clone();

        for (int x=1; x < image.rows() - 1; x++)
        {
            for (int y=1; y < image.cols() - 1; y++)
            {
                int[] diffs = new int[8];
                // Find the differences between the current center pixel and its neighbours
                double center = image.get(x,y)[0]; // Starting from the same row, right column pixel, clockwise
                diffs[0] = (Double.compare(image.get(x,y+1)[0], center) > 0) ? 1 : 0;
                diffs[1] = (Double.compare(image.get(x+1,y+1)[0],center) > 0) ? 1 : 0;
                diffs[2] = (Double.compare(image.get(x+1,y)[0],center) > 0) ? 1 : 0;
                diffs[3] = (Double.compare(image.get(x+1,y-1)[0],center) > 0) ? 1 : 0;
                diffs[4] = (Double.compare(image.get(x,y-1)[0],center) > 0) ? 1 : 0;
                diffs[5] = (Double.compare(image.get(x-1,y-1)[0],center) > 0) ? 1 : 0;
                diffs[6] = (Double.compare(image.get(x-1,y)[0],center) > 0) ? 1 : 0;
                diffs[7] = (Double.compare(image.get(x-1,y+1)[0],center) > 0) ? 1 : 0;

                int decimal = 0;
                int power = 1;
                for (int i=0; i < diffs.length; i++)
                {
                    if (diffs[i] == 1) {
                        decimal += power;
                    }
                    power *= 2;
                }

                // If binary pattern is uniform increase is hist value otherwise increase the NON_UNIFORM_BIN
                if (isBinaryUniform(convertToBinary(decimal))){ // Key already exists, so increment its value
                    histogram.put(decimal,histogram.get(decimal)+1);
                }
                else {
                    histogram.put(NON_UNIFORM_BIN,histogram.get(NON_UNIFORM_BIN)+1);
                }
            }
        }
        IntArrayList keyList = new IntArrayList();
        IntArrayList valueList = new IntArrayList();
        histogram.pairsSortedByKey(keyList,valueList);
        return valueList.elements(); // returning the 59 freq-values of the histogram
    }

}
