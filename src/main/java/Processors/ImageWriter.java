package Processors;

import Entities.ApplicationPaths;
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
        String filePath = Paths.get(ApplicationPaths.RESOURCES_OUTPUTS, ApplicationPaths.UNIQUE_FOLDER_NAME,
                "Text Blocks", fileCounter++ + ".png").toAbsolutePath().toString();
        MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
        Imgcodecs.imwrite(filePath,frame,params);
        return new File(filePath);
    }

    public static void writePaintedFrame(Mat frame)
    {
        if (!writingEnabled) return;
        String filePath = Paths.get(ApplicationPaths.RESOURCES_OUTPUTS, ApplicationPaths.UNIQUE_FOLDER_NAME,
                "Painted Frames", fileCounter++ + ".png").toAbsolutePath().toString();
        MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
        Imgcodecs.imwrite(filePath,frame,params);
    }

    public static void writeOCRImage(Mat frame)
    {
        if (!writingEnabled) return;
        String filePath = Paths.get(ApplicationPaths.RESOURCES_OUTPUTS, ApplicationPaths.UNIQUE_FOLDER_NAME,
                "OCR Images", fileCounter++ + ".png").toAbsolutePath().toString();
        MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
        Imgcodecs.imwrite(filePath,frame,params);
    }

    public static void writeTemp(Mat frame)
    {
        String filePath = Paths.get(ApplicationPaths.RESOURCES_OUTPUTS, ApplicationPaths.UNIQUE_FOLDER_NAME,
                "Temp", fileCounter++ + ".png").toAbsolutePath().toString();
        MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
        Imgcodecs.imwrite(filePath,frame,params);
    }

    public static void writeStep(Mat frame)
    {
        if (!writingEnabled) return;
        String filePath = Paths.get(ApplicationPaths.RESOURCES_OUTPUTS, ApplicationPaths.UNIQUE_FOLDER_NAME,
                "Steps", fileCounter++ + ".png").toAbsolutePath().toString();
        MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
        Imgcodecs.imwrite(filePath,frame,params);
    }

    public static void setWritingEnabled(boolean writingEnabled) {
        ImageWriter.writingEnabled = writingEnabled;
    }
}


