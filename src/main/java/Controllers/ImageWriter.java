package Controllers;

import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

import java.io.File;

/**
 * Created by arxa on 26/2/2017.
 */

public class ImageWriter
{
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

    public static File writeImage(Mat frame, String fullPath)
    {
        MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
        Imgcodecs.imwrite(fullPath,frame,params);
        return new File(fullPath);
    }

    public static File writePaintedFrame(Mat frame)
    {
        String filePath = folderPath + uniqueFolderName +"\\Painted Frames\\"+ fileCounter++ + ".png";
        MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
        Imgcodecs.imwrite(filePath,frame,params);
        return new File(filePath);
    }

    public static File writeStep(Mat frame)
    {
        String filePath = folderPath + uniqueFolderName +"\\Steps\\"+ fileCounter++ + ".png";
        MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
        Imgcodecs.imwrite(filePath,frame,params);
        return new File(filePath);
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
}
