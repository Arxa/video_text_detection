package processors;

import entities.ApplicationPaths;
import entities.OutputFolderNames;
import org.jetbrains.annotations.NotNull;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import java.io.File;
import java.nio.file.Paths;

/**
 * Created by arxa on 26/2/2017.
 */

public class ImageWriter
{
    private static boolean writingEnabled = false;
    private static int fileCounter;

    @NotNull
    public static File writeTextBlock(Mat frame)
    {
        String filePath = Paths.get(ApplicationPaths.RESOURCES_OUTPUTS, ApplicationPaths.UNIQUE_OUTPUT_FOLDER_NAME,
                OutputFolderNames.ocr_images.name(), fileCounter++ + ".png").toAbsolutePath().toString();
        MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
        Imgcodecs.imwrite(filePath,frame,params);
        return new File(filePath);
    }

    public static void writeOutputImage(Mat image,OutputFolderNames outputFolderName){
        if (!writingEnabled) return;
        String filePath = Paths.get(ApplicationPaths.RESOURCES_OUTPUTS, ApplicationPaths.UNIQUE_OUTPUT_FOLDER_NAME,
                outputFolderName.name(), fileCounter++ + ".png").toAbsolutePath().toString();
        MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
        Imgcodecs.imwrite(filePath,image,params);
    }

    public static void setWritingEnabled(boolean writingEnabled) {
        ImageWriter.writingEnabled = writingEnabled;
    }
}


