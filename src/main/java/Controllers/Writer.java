package Controllers;

import Models.CacheID;
import org.opencv.core.CvType;
import org.opencv.core.Size;
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
    private static VideoWriter output;
    private static File processedVideo;

    public static void writeFrames()
    {
        /* Is this required?
        try {
            System.load("C:\\Users\\arxa\\Desktop\\dll\\openh264-1.4.0-win64msvc.dll");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
        } */
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

            // System.out.println(getFromCache(finalI).getFrame().type());
            // We need to convert out frame type to number 8(check opencv format table) in order to write it.
            SystemController.getFromCache(id).getFrame().convertTo
                    (SystemController.getFromCache(id).getFrame(), CvType.CV_8UC2);
            output.write(SystemController.getFromCache(id).getFrame());
        }
    }

    public static void initializeVideoWriter()
    {
        String filepath = "src\\main\\resources\\Outputs\\"+Player.getFilename().getName().replace(".mp4","")+" "+
                new Date().toString().replace(":","-")+".mp4";
        int fourcc = VideoWriter.fourcc('X','2','6','4');
        // This fourcc code works with .mp4 | Another working set is DIV3 with .avi
        output = new VideoWriter(filepath,fourcc, VideoProcessor.getCap().get(CAP_PROP_FPS),
                new Size(VideoProcessor.getCap().get(CAP_PROP_FRAME_WIDTH),
                        VideoProcessor.getCap().get(CAP_PROP_FRAME_HEIGHT)),true);

        //String filePath2 = "C:\\Users\\arxa\\Desktop\\InteliJ IDEA\\Thesis\\"+filepath;
        processedVideo = new File(filepath);
    }

    public static VideoWriter getOutput() {
        return output;
    }

    public static File getProcessedVideo() {
        return processedVideo;
    }
}
