package Processors;

import Entities.ApplicationPaths;
import org.jetbrains.annotations.NotNull;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import java.io.File;

/**
 * Created by arxa on 26/2/2017.
 */

/**
 * This class is used to write and export specific parts of the video processing to
 * corresponding application directories
 */
public class ImageWriter
{
    private static boolean writingEnabled = false;
    private static int fileCounter;

    @NotNull
    public static File writeTextBlock(Mat frame)
    {
        String filePath = ApplicationPaths.FOLDER_PATH + ApplicationPaths.FILE_SEPERATOR + ApplicationPaths.UNIQUE_FOLDER_NAME + ApplicationPaths.FILE_SEPERATOR +
                "Text Blocks" + ApplicationPaths.FILE_SEPERATOR + fileCounter++ + ".png";
        MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
        Imgcodecs.imwrite(filePath,frame,params);
        return new File(filePath);
    }

    public static void writePaintedFrame(Mat frame)
    {
        if (!writingEnabled) return;
        String filePath = ApplicationPaths.FOLDER_PATH + ApplicationPaths.FILE_SEPERATOR + ApplicationPaths.UNIQUE_FOLDER_NAME + ApplicationPaths.FILE_SEPERATOR +
                "Painted Frames" + ApplicationPaths.FILE_SEPERATOR + fileCounter++ + ".png";
        MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
        Imgcodecs.imwrite(filePath,frame,params);
    }

    public static void writeOCRImage(Mat frame)
    {
        if (!writingEnabled) return;
        String filePath = ApplicationPaths.FOLDER_PATH + ApplicationPaths.FILE_SEPERATOR + ApplicationPaths.UNIQUE_FOLDER_NAME + ApplicationPaths.FILE_SEPERATOR +
                "OCR Images" + ApplicationPaths.FILE_SEPERATOR + fileCounter++ + ".png";
        MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
        Imgcodecs.imwrite(filePath,frame,params);
    }

    public static void writeStep(Mat frame)
    {
        if (!writingEnabled) return;
        String filePath = ApplicationPaths.FOLDER_PATH + ApplicationPaths.FILE_SEPERATOR + ApplicationPaths.UNIQUE_FOLDER_NAME + ApplicationPaths.FILE_SEPERATOR +
                "Steps" + ApplicationPaths.FILE_SEPERATOR + fileCounter++ + ".png";
        MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
        Imgcodecs.imwrite(filePath,frame,params);
    }

    public static void setWritingEnabled(boolean writingEnabled) {
        ImageWriter.writingEnabled = writingEnabled;
    }
}


