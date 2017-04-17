package Controllers;

import Models.Region;
import Models.SubRegion;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by arxa on 2/4/2017.
 */

public class Training
{
    public static final int FEATURE_DIMENSIONS = 59;
    public static final int SUBREGIONS = 500;
    private static final String DATA_PATH = "src\\main\\resources\\Testing_Dataset";
    private static final String DATA_TYPE = ".png";
    private static int number_of_text_data;
    private static int number_of_background_data;

    private static Mat training_set;
    private static Mat labels;

    public static void init()
    {
        try
        {
            number_of_text_data = (int) Files.list(Paths.get(DATA_PATH+"\\"+"text")).count();
            number_of_background_data = (int) Files.list(Paths.get(DATA_PATH+"\\"+"background")).count();
            training_set = new Mat((number_of_text_data + number_of_background_data) * SUBREGIONS,FEATURE_DIMENSIONS, CvType.CV_32FC1);
            labels = new Mat((number_of_text_data + number_of_background_data) * SUBREGIONS, 1, CvType.CV_32FC1);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void create_labels()
    {
        for (int i=0; i < number_of_text_data * SUBREGIONS; i++) {
            labels.put(i,0,1.0);
        }

        for (int i = number_of_text_data * SUBREGIONS;
             i < ((number_of_background_data + number_of_text_data) * SUBREGIONS); i++) {
            labels.put(i,0,0.0);
        }
    }

    public static void read_training_data(String type)
    {
        // starting_point helps us append our data to train[][]. First our text data and then our background data
        int starting_point;
        if (type.equals("text")) starting_point = 0;
        else starting_point = number_of_text_data * SUBREGIONS;

        int file_counter = 0;
        Map<Integer,Integer> histogram;
        URLBP.generateFeatureBaseVector();
        while(true)
        {
            String filePath = DATA_PATH + "\\"+ type + "\\" + Integer.toString(file_counter++) + DATA_TYPE;
            File file = new File(filePath);
            if (file.exists() && !file.isDirectory())
            {
                Mat train_image = Imgcodecs.imread(filePath);
                Mat adjusted = MatProcessor.change_mat_resolution(train_image, 500, 100);
                Mat adjusted_gray = new Mat();
                Imgproc.cvtColor(adjusted, adjusted_gray, Imgproc.COLOR_RGB2GRAY, 0);
                Region region = new Region(adjusted_gray);  // Our Region is the whole train image here.
                PixelProcessor.crop_region_into_subregions(region);

                for (SubRegion s : region.getSubregions())
                {
                    int y = 0;
                    histogram = URLBP.calculateURLBP(s.getMatSubregion());
                    for (Integer key : histogram.keySet())
                    {
                        training_set.put(starting_point,y,(double) histogram.get(key));
                        y++;
                    }
                    starting_point++;
                }
            }
            else{
                System.out.println("File does not exist or it's a directory");
                break;
            }
            System.out.println("Text database was read.");
        }
    }

    public static int getNumber_of_text_data() {
        return number_of_text_data;
    }

    public static int getNumber_of_background_data() {
        return number_of_background_data;
    }

    public static Mat getTraining_set() {
        return training_set;
    }

    public static Mat getLabels() {
        return labels;
    }
}
