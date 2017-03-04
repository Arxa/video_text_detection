package Controllers;

import Models.CacheID;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoWriter;

import java.io.File;
import java.util.Date;
import static org.opencv.videoio.Videoio.CAP_PROP_FPS;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_WIDTH;

/**
 * Created by arxa on 26/2/2017.
 */

public class Writer
{
    private static String currentVideoFolderName;
    private static int binaryFrameCounter;
    private static final String fixedOutputPath = "src\\main\\resources\\Outputs\\";
    private static VideoWriter output;
    private static File processedVideo;

    public static void writeFramesToVideo()
    {
        for (int id : CacheID.cacheIds.elements())
        {
                        /* ----Converting Mat to Image----
                        MatOfByte byteMat1 = new MatOfByte();
                        Imgcodecs.imencode(".bmp", getFromCache(i).getFrame(), byteMat1);
                        Image image1 = new Image(new ByteArrayInputStream(byteMat1.toArray()));
                        ImageView iv1 = new ImageView(image1);
                        ----Show frame on Scene----
                        pane2.getChildren().add(iv1);
                        iv1.fitHeightProperty().bind(pane2.heightProperty());
                        iv1.fitWidthProperty().bind(pane2.widthProperty()); */

            // We need to convert out frame type to number 8(check opencv format table) in order to write it.
            SystemController.getFromCache(id).getFrame().convertTo
                    (SystemController.getFromCache(id).getFrame(), CvType.CV_8UC2);
            output.write(SystemController.getFromCache(id).getFrame());
        }
    }

    public static void initializeVideoWriter()
    {
        String filepath = fixedOutputPath+currentVideoFolderName+"\\"+currentVideoFolderName+".mp4";
        int fourcc = VideoWriter.fourcc('X','2','6','4');
        // This fourcc code works with .mp4 | Another working set is DIV3 with .avi
        output = new VideoWriter(filepath,fourcc, VideoProcessor.getCap().get(CAP_PROP_FPS),
                new Size(VideoProcessor.getCap().get(CAP_PROP_FRAME_WIDTH),
                        VideoProcessor.getCap().get(CAP_PROP_FRAME_HEIGHT)),true);
        processedVideo = new File(filepath);
    }

    public static void writeFrameAsImage(Mat frame)
    {
        MatOfInt params = new MatOfInt(Imgcodecs.CV_IMWRITE_PNG_COMPRESSION);
        Imgcodecs.imwrite(fixedOutputPath+currentVideoFolderName+"\\"+ ++binaryFrameCounter+" - "+
                currentVideoFolderName+".png",frame,params);
    }


    public static VideoWriter getOutput() {
        return output;
    }

    public static File getProcessedVideo() {
        return processedVideo;
    }

    public static String getCurrentVideoFolderName() {
        return currentVideoFolderName;
    }

    public static void setCurrentVideoFolderName(String outputCurrentPath) {
        Writer.currentVideoFolderName = outputCurrentPath;
    }

    public static String getFixedOutputPath() {
        return fixedOutputPath;
    }

    public static int getBinaryFrameCounter() {
        return binaryFrameCounter;
    }

    public static void setBinaryFrameCounter(int binaryFrameCounter) {
        Writer.binaryFrameCounter = binaryFrameCounter;
    }
}
