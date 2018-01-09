package svm;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Training {

    private static final int FEATURE_DIMENSIONS = 59;

    public static double[] getLabels(int dataCounter){
        double[] labels = new double[dataCounter];
        for (int i = 0; i < labels.length; i++){
            labels[i] = 0;
        }
        return labels;
    }

    public static double[][] getTrainingData(String path)
    {
        File dir = new File(path);
        int numberOfData = dir.listFiles().length;
        double[][] training_data = new double[numberOfData*50][FEATURE_DIMENSIONS];

        int regionCounter = 0;
        int featureCounter = 0;
        for (File img : dir.listFiles())
        {
            Mat src = Imgcodecs.imread(img.getAbsolutePath());
            for (Mat subregion : getSubregions(src)){
                for (int feature : LocalBinaryPattern.get_ULBP_Features(subregion)){
                    training_data[regionCounter][featureCounter++] = (double) feature;
                }
                featureCounter = 0;
                regionCounter++;
            }
        }
        for (double[] data : training_data) {
            normalizeArray(data);
        }
        return training_data;
    }

    public static double[] intArrayToDouble(int[] array){
        double[] dArray = new double[array.length];
        for (int i=0; i < array.length; i++){
            dArray[i] = (double)array[i];
        }
        return dArray;
    }

    public static List<Mat> getSubregions(Mat image){
        List<Mat> subregions = new ArrayList<>();
        for (int i=0; i<image.rows(); i=i+10){
            for (int j=0; j<image.cols(); j=j+10){
                Rect crop = new Rect(new Point(j,i),new Size(10,10)); // points coords are reversed! Point(column,row)
                Mat region = new Mat(image,crop);
                subregions.add(region);
            }
        }
        return subregions;
    }

    public static void normalizeArray(double[] array){
        double max = array[0];
        double min = array[0];
        for (int i=1; i < array.length; i++){
            if (Double.compare(array[i],max) > 0) max = array[i];
            if (Double.compare(array[i],min) < 0) min = array[i];
        }
        for (int i=0; i < array.length; i++){
            array[i] = (array[i] - min) / (max - min);
        }
    }
}
