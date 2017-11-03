package Processors;

import net.sf.javaml.utils.ArrayUtils;
import org.jetbrains.annotations.Contract;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import java.io.File;

public class Training {

    public static final int FEATURE_DIMENSIONS = 59;

    @Contract(pure = true)
    public static double[] getLabels(int dataCounter){
        double[] labels = new double[dataCounter];
        for (int i = 0; i < labels.length; i++){
            labels[i] = 0;
        }
        return labels;
    }

    public static double[][] getTrainingData()
    {
        File dir = new File("C:\\Users\\310297685\\Desktop\\Text_Dataset\\text");
        int numberOfData = dir.listFiles().length;
        double[][] training_data = new double[numberOfData][FEATURE_DIMENSIONS];


        int fileCounter = 0;
        for (File img : dir.listFiles())
        {
            int featureCounter = 0;
            Mat src = Imgcodecs.imread(img.getAbsolutePath());
            for (int feature : URLBP.getURLBFeatures(src)){
                training_data[fileCounter][featureCounter++] = (double) feature;
            }
//            for (double feature : getColorFeatures(src)){
//                training_data[fileCounter][featureCounter++] = feature;
//            }
            fileCounter++;
        }
        for (double[] data : training_data) {
            ArrayUtils.normalize(data);
        }
        return training_data;
    }

    public static double[] getColorFeatures(Mat image){
        double[] data = new double[image.rows() * image.cols()];
        int counter = 0;
        for (int i = 0; i < image.rows(); i++){
            for (int j = 0; j < image.cols(); j++){
                data[counter++] = image.get(i,j)[0];
            }
        }
        return data;
    }
}
