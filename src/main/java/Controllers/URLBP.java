package Controllers;

import org.opencv.core.Mat;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by arxa on 30/3/2017.
 */

public class URLBP // Uniform Rotated Local Binary Pattern
{
    private static final int NON_UNIFORM_BIN = 999;

    // 59 dimensions feature vector, based on the URLBP histogram
    private static Map<Integer,Integer> subregion_lbp_histogram_template;

    public static Map<Integer,Integer> calculateURLBP(Mat subregion)
    {
        // Using: R(Radius) = 1, P(Pixel Neighbours) = 8

        Map<Integer,Integer> subregion_lbp_histogram = new TreeMap<>();
        subregion_lbp_histogram.putAll(subregion_lbp_histogram_template);

        for (int x=1; x < subregion.width() - 1; x++)
        {
            for (int y=1; y < subregion.height() - 1; y++)
            {
                double[] diffs = new double[8];
                int binary_pattern = 0;

                double center = subregion.get(x,y)[0];

                diffs[0] = subregion.get(x,y+1)[0] - center;
                diffs[1] = subregion.get(x+1,y+1)[0] - center;
                diffs[2] = subregion.get(x+1,y)[0] - center;
                diffs[3] = subregion.get(x+1,y-1)[0] - center;
                diffs[4] = subregion.get(x,y-1)[0] - center;
                diffs[5] = subregion.get(x-1,y-1)[0] - center;
                diffs[6] = subregion.get(x-1,y)[0] - center;
                diffs[7] = subregion.get(x-1,y+1)[0] - center;

                double max = -1;
                int dominantDirection = -1;
                for (int i=0; i < 8; i++)
                {
                    if (diffs[i] > max){
                        max = diffs[i];
                        dominantDirection = i;
                    }
                }

                if (max == -1)
                {
                    subregion_lbp_histogram.compute(binary_pattern,(k, v) -> v + 1);
                    continue;
                }

                int power = 1;
                for (int i=dominantDirection; i < 8; i++)
                {
                    if (Double.compare(diffs[i],0.0) >= 0) {
                        binary_pattern += power;
                    }
                    power *= 2;
                }

                for (int i=0; i < dominantDirection; i++)
                {
                    if (Double.compare(diffs[i],0.0) >= 0){
                        binary_pattern += power;
                    }
                    power *= 2;
                }

                if (isBinaryUniform(convertTobinary(binary_pattern))){ // Key already exists, so increment its value
                    subregion_lbp_histogram.compute(binary_pattern,(k, v) -> v + 1);
                }
                else {
                    subregion_lbp_histogram.compute(NON_UNIFORM_BIN,(k, v) -> v + 1);
                }
            }
        }
        return subregion_lbp_histogram;
    }

    public static boolean isBinaryUniform(int[] binary)
    {
        int transitions = 0;
        for (int i=0; i < binary.length - 1; i++)
        {
            if (binary[i] != binary[i+1])
            {
                transitions++;
            }
        }
        return transitions <= 2;
    }

    public static void generateFeatureBaseVector() // Should be called only one time
    {
        subregion_lbp_histogram_template = new TreeMap<>();
        subregion_lbp_histogram_template.put(NON_UNIFORM_BIN,0); // Single bin for non-uniform patterns

        for (int i=0; i <= 255; i++) // Placing bins for uniform patterns separately. Map size should be 59
        {
            if (isBinaryUniform(convertTobinary(i))){
                subregion_lbp_histogram_template.put(i,0);
            }
        }
    }

    public static int[] convertTobinary(int number)
    {
        int[] binary = new int[8];
        for (int i = 7, num = number; i >= 0; i--, num >>>= 1)
            binary[i] = num & 1;
        return binary;
    }
}
