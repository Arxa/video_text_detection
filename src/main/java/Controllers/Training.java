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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by arxa on 2/4/2017.
 */

public class Training
{
    public static final int FEATURE_DIMENSIONS = 1;
    public static final int SUBREGIONS = 500;
    private static final String DATA_PATH = "src\\main\\resources\\Training_Dataset";
    private static int number_of_text_data;
    private static int number_of_background_data;
    private static Mat training_set;
    private static Mat labels;

    public static void init()
    {
        try {
            number_of_text_data = (int) Files.list(Paths.get(DATA_PATH+"\\"+"text")).count();
            number_of_background_data = (int) Files.list(Paths.get(DATA_PATH+"\\"+"background")).count();
            int nontext_rows = number_of_background_data * SUBREGIONS;
            training_set = new Mat(number_of_text_data + nontext_rows, FEATURE_DIMENSIONS, CvType.CV_32FC1);
            labels = new Mat(number_of_text_data + nontext_rows, 1, CvType.CV_32FC1);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void create_labels()
    {
        for (int i=0; i < number_of_text_data; i++) {
            labels.put(i,0,1.0);
        }

        for (int i = number_of_text_data; i < labels.rows(); i++) {
            labels.put(i,0,-1.0);
        }
    }

    public static void read_training_data(String type) throws IOException
    {
        // starting_point helps us append our data to train[][]. First our text data and then our background data
        int starting_point;

        if (type.equals("text")) starting_point = 0;
        else starting_point = number_of_text_data;

        Map<Integer,Integer> histogram;
        URLBP.generateFeatureBaseVector();

        String filePath = DATA_PATH + "\\"+ type + "\\";

        List<Path> pathsToFiles = Files.walk(Paths.get(filePath))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());

        // Images are already adjusted TODO Consider Interface here.
        if (type.equals("text"))
        {
            for (Path p : pathsToFiles)
            {
                int y = 0;
                int whites = 0;
                Mat train_image = Imgcodecs.imread(p.toString());
                for (int i=0; i < train_image.rows(); i++) {
                    for (int j=0; j < train_image.cols(); j++) {
                        if (Double.compare(train_image.get(i,j)[0],0.0) > 0){
                            whites++;
                        }
                    }
                }
                training_set.put(starting_point++,0,(double)whites);
                /*Mat gray = new Mat();
                Imgproc.cvtColor(train_image, gray, Imgproc.COLOR_RGB2GRAY, 0);
                histogram = URLBP.calculateURLBP(gray);
                for (Integer key : histogram.keySet()) {
                    training_set.put(starting_point++,y++,(double) histogram.get(key));
                }*/
                System.out.println("An text Image was read");
            }
        }
        else
        {
            for (Path p : pathsToFiles)
            {
                Mat train_image = Imgcodecs.imread(p.toString());
                Mat adjusted = MatProcessor.change_mat_resolution(train_image, 500, 100);
                Mat adjusted_gray = new Mat();
                //Imgproc.cvtColor(adjusted, adjusted_gray, Imgproc.COLOR_RGB2GRAY, 0);
                Region region = new Region(adjusted);  // Our Region is the whole train image here.
                PixelProcessor.crop_region_into_subregions(region);

                for (SubRegion s : region.getSubregions())
                {
                    int whites = 0;
                    for (int i=0; i < s.getMatSubregion().rows(); i++) {
                        for (int j=0; j < s.getMatSubregion().cols(); j++) {
                            if (Double.compare(train_image.get(i,j)[0],0.0) > 0){
                                whites++;
                            }
                        }
                    }
                    training_set.put(starting_point++,0,(double)whites);
                    /*
                    int y = 0;
                    histogram = URLBP.calculateURLBP(s.getMatSubregion());
                    for (Integer key : histogram.keySet()) {
                        training_set.put(starting_point++,y++,(double) histogram.get(key));
                    }*/
                }
                System.out.println("An Image was read");
            }
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
