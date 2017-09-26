package Processors;

import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;

/**
 * Created by arxa on 26/2/2017.
 */

public class ImageWriter
{
    private static boolean writingEnabled = false;
    private static final String folderPath = "src\\main\\resources\\Outputs\\";
    private static String uniqueFolderName;
    private static int fileCounter;

    public static File writeTextBlock(Mat frame)
    {
        String filePath = folderPath + uniqueFolderName +"\\Text Blocks\\"+ fileCounter++ + ".png";
        MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
        Imgcodecs.imwrite(filePath,frame,params);
        return new File(filePath);
    }

    public static void writePaintedFrame(Mat frame)
    {
        if (!writingEnabled) return;
        String filePath = folderPath + uniqueFolderName +"\\Painted Frames\\"+ fileCounter++ + ".png";
        MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
        Imgcodecs.imwrite(filePath,frame,params);
    }

    public static void writeOCRImage(Mat frame)
    {
        if (!writingEnabled) return;
        String filePath = folderPath + uniqueFolderName +"\\OcrProcessor Images\\"+ fileCounter++ + ".png";
        MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
        Imgcodecs.imwrite(filePath,frame,params);
    }

    public static void writeStep(Mat frame)
    {
        if (!writingEnabled) return;
        String filePath = folderPath + uniqueFolderName +"\\Steps\\"+ fileCounter++ + ".png";
        MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
        Imgcodecs.imwrite(filePath,frame,params);
    }


    public static String getUniqueFolderName() {
        return uniqueFolderName;
    }

    public static void setUniqueFolderName(String outputCurrentPath) {
        ImageWriter.uniqueFolderName = outputCurrentPath;
    }

    public static String getFolderPath() {
        return folderPath;
    }

    public static String getUniquePath(){
        return folderPath + uniqueFolderName;
    }

    public static void setWritingEnabled(boolean writingEnabled) {
        ImageWriter.writingEnabled = writingEnabled;
    }
}


