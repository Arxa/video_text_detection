package Processors;


import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntIntHashMap;
import org.jetbrains.annotations.Contract;
import org.opencv.core.Mat;

public class URLBP {

    private static final int NON_UNIFORM_BIN = 999;

    // 59 dimensions feature vector, based on the URLBP histogram
    private static OpenIntIntHashMap histogramTemplate = getHistogramTemplate();

    public static int[] getURLBFeatures(Mat image)
    {
        // Using: R(Radius) = 1, P(Pixel Neighbours) = 8
        OpenIntIntHashMap histogram = (OpenIntIntHashMap) histogramTemplate.clone();

        for (int x=1; x < image.rows() - 1; x++)
        {
            for (int y=1; y < image.cols() - 1; y++)
            {
                double[] diffs = new double[8];
                double center = image.get(x,y)[0];

                diffs[0] = (Double.compare(image.get(x,y+1)[0], center) != 0) ? Math.abs(image.get(x,y+1)[0] - center) : 0.0;
                diffs[1] = (Double.compare(image.get(x+1,y+1)[0],center) != 0) ? Math.abs(image.get(x+1,y+1)[0] - center) : 0.0;
                diffs[2] = (Double.compare(image.get(x+1,y)[0],center) != 0) ? Math.abs(image.get(x+1,y)[0] - center) : 0.0;
                diffs[3] = (Double.compare(image.get(x+1,y-1)[0],center) != 0) ? Math.abs(image.get(x+1,y-1)[0] - center) : 0.0;
                diffs[4] = (Double.compare(image.get(x,y-1)[0],center) != 0) ? Math.abs(image.get(x,y-1)[0] - center) : 0.0;
                diffs[5] = (Double.compare(image.get(x-1,y-1)[0],center) != 0) ? Math.abs(image.get(x-1,y-1)[0] - center) : 0.0;
                diffs[6] = (Double.compare(image.get(x-1,y)[0],center) != 0) ? Math.abs(image.get(x-1,y)[0] - center) : 0.0;
                diffs[7] = (Double.compare(image.get(x-1,y+1)[0],center) != 0) ? Math.abs(image.get(x-1,y+1)[0] - center) : 0.0;

                double max = -1;
                int dominantDirection = -1;
                for (int i=0; i < diffs.length-1; i++)
                {
                    if (Double.compare(diffs[i],0.0) > 0){
                        if (Double.compare(diffs[i],max) > 0){
                            max = diffs[i];
                            dominantDirection = i;
                        }
                    }
                }

                if (max == -1) {
                    histogram.put(0,histogram.get(0)+1);
                    continue;
                }

                int binary_pattern = 0;
                int power = 1;
                for (int i=dominantDirection; i < diffs.length-1; i++)
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
        return valueList.elements();
    }

    @Contract(pure = true)
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


    @Contract(pure = true)
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

}
