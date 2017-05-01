package Controllers;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoWriter;

import java.io.File;

import static org.opencv.videoio.Videoio.CAP_PROP_FPS;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_WIDTH;

/**
 * Created by arxa on 26/2/2017.
 */

public class Writer
{
    private static final String folderPath = "src\\main\\resources\\Outputs\\";
    private static String uniqueFolderName;
    private static int fileCounter;
    private static VideoWriter output;
    private static String fullVideoFilePath;

    public static void writeFrameToVideo(Mat frame)
    {
        /*
        We need to convert our frame type to number 8 i.e. 8UC2 (check opencv format table)
         */
        frame.convertTo(frame, CvType.CV_8UC2);
        try {
            output.write(frame);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void initializeVideoWriter()
    {
        fullVideoFilePath = folderPath + uniqueFolderName + "\\"+uniqueFolderName +".mp4";
        /*
        This fourcc code works with .mp4. The outcome is unknown for different combinations
         */
        int fourcc = VideoWriter.fourcc('X','2','6','4');
        output = new VideoWriter(fullVideoFilePath, fourcc, VideoProcessor.getCap().get(CAP_PROP_FPS),
                new Size(VideoProcessor.getCap().get(CAP_PROP_FRAME_WIDTH),
                        VideoProcessor.getCap().get(CAP_PROP_FRAME_HEIGHT)),true);
    }

    public static File writeTextBlock(Mat frame)
    {
        String filePath = folderPath + uniqueFolderName +"\\Text Blocks\\"+ fileCounter++ + ".png";
        MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
        Imgcodecs.imwrite(filePath,frame,params);
        return new File(filePath);
    }

    public static File writePaintedFrame(Mat frame)
    {
        String filePath = folderPath + uniqueFolderName +"\\Painted Frames\\"+ fileCounter++ + ".png";
        MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
        Imgcodecs.imwrite(filePath,frame,params);
        return new File(filePath);
    }


    public static VideoWriter getOutput() {
        return output;
    }

//    public static File getProcessedVideo() {
//        return processedVideo;
//    }

    public static String getUniqueFolderName() {
        return uniqueFolderName;
    }

    public static void setUniqueFolderName(String outputCurrentPath) {
        Writer.uniqueFolderName = outputCurrentPath;
    }

    public static int getFileCounter() {
        return fileCounter;
    }

    public static void setFileCounter(int fileCounter) {
        Writer.fileCounter = fileCounter;
    }

    public static String getFullVideoFilePath() {
        return fullVideoFilePath;
    }

    public static String getFolderPath() {
        return folderPath;
    }
}
