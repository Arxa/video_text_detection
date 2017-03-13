package Controllers;

;
import Models.ImageContainer;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.List;


/**
 * Created by arxa on 16/11/2016.
 */

public class VideoProcessor
{
    private static VideoCapture cap;

    // TODO Handle the Exceptions
    public static void processVideo(Pane pane2, Label label1)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Required for OpenCV libraries usage
        cap = new VideoCapture();
        cap.open(Player.getFilename().toString());

        if (cap != null && cap.isOpened())
        {
            Task<Void> task = new Task<Void>()
            {
                @Override protected Void call() throws Exception
                {
                    Writer.initializeVideoWriter();
                    ImageContainer.init();
                    boolean open1 = cap.read(ImageContainer.getInput());

                    while (true)
                    {
                        if (open1)
                        {
                            ImageContainer.setInput_GB(new Mat(ImageContainer.getInput().size(),ImageContainer.getInput().type()));

                            Imgproc.GaussianBlur(ImageContainer.getInput(),ImageContainer.getInput_GB(),
                                    new Size(15.0,15.0),0.0,0.0);

                            Imgproc.cvtColor(ImageContainer.getInput_GB(), ImageContainer.getInput_GB_Gray(), Imgproc.COLOR_RGB2GRAY, 0);

                            Imgproc.Laplacian(ImageContainer.getInput_GB_Gray(),ImageContainer.getInput_GB_Gray_LPL(),
                                    CvType.CV_16S,1,1,0); //bigger ksize stronger intensity

                            // array allocation happens inside 'matToArray' method
                            ImageContainer.setInput_GB_Gray_LPL_in_Array
                                    (PixelProcessor.matToArray(ImageContainer.getInput_GB_Gray_LPL()));

                            ImageContainer.setInput_GB_Gray_LPL_MGD_in_Array(PixelProcessor.
                                    find_MaximumGradientDifference(ImageContainer.getInput_GB_Gray_LPL_in_Array(),
                                            ImageContainer.getInput_GB_Gray_LPL().height(),
                                                    ImageContainer.getInput_GB_Gray_LPL().width()));

                            ImageContainer.setInput_GB_Gray_LPL_MGD(PixelProcessor.
                                    arrayToMat(ImageContainer.getInput_GB_Gray_LPL_MGD_in_Array(),
                                            ImageContainer.getInput_GB_Gray_LPL().height(),ImageContainer.getInput_GB_Gray_LPL().width()));

                            ImageContainer.getInput_GB_Gray_LPL_MGD_NORM().create
                                    (ImageContainer.getInput_GB_Gray_LPL_MGD().size(),CvType.CV_32FC1);

                            Core.normalize(ImageContainer.getInput_GB_Gray_LPL_MGD(),
                                    ImageContainer.getInput_GB_Gray_LPL_MGD_NORM(),0.0,1.0,
                                            Core.NORM_MINMAX,ImageContainer.getInput_GB_Gray_LPL_MGD_NORM().type());

                            // Make Classification of 0~1 normalized values into 0 and 1
                            ImageContainer.setInput_GB_Gray_LPL_MGD_NORM_KMEANS
                                    (Clustering.k_Means(ImageContainer.getInput_GB_Gray_LPL_MGD_NORM()));

                            // Initializing 'Binary' image filled with zeros (black)
                            ImageContainer.setInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN
                                    (new Mat(ImageContainer.getInput_GB_Gray_LPL_MGD_NORM().size(),
                                            CvType.CV_8UC1,new Scalar(0.0)));

                            // Fill the K-MEANS text cluster values to binary image above as ones (white)
                            Visualizer.paintMatToBinary(ImageContainer.
                                            getInput_GB_Gray_LPL_MGD_NORM_KMEANS(),
                                                    ImageContainer.getInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN());

                            // Initialize image for the Dilation operation
                            ImageContainer.setInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED(new Mat
                                    (ImageContainer.getInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN().rows(),
                                            ImageContainer.getInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN().cols(),
                                                    ImageContainer.getInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN().type()));

                            // Do Dilation
                            Imgproc.dilate(ImageContainer.getInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN(),
                                    ImageContainer.getInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED(),
                                        Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(13.0,13.0)));
                                        // Defaults: 3x3 - Anchor: center

                            // Calculate Sobel Edges of original image - needed below
                            Imgproc.Sobel(ImageContainer.getInput(),ImageContainer.getInput_Sobel(),CvType.CV_8U,1,1);

                            // Find and Filter Text Rect Blocks - with Sobel's help
                            List<Rect> textBlocks = Clustering.find_TextBlocks
                                    (ImageContainer.getInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED());

                            // TODO consider changing the size of every new Mat to original input's only

                            // Initializing 'Binary' image filled with zeros (black)
                            ImageContainer.setInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED_FILTERED
                                    (new Mat(ImageContainer.getInput_GB_Gray_LPL_MGD_NORM().size(),
                                            CvType.CV_8UC1,new Scalar(0.0)));

                            // Paint the approved text blocks to the original image
                            Visualizer.paintBlocksToOriginalImage(textBlocks, ImageContainer.getInput());

                            // Create Image file
                            Writer.writeFrameAsImage(ImageContainer.getInput());

                            System.out.println("Cycle completed");
                            ImageContainer.init();

                            open1 = cap.read(ImageContainer.getInput());

                        }
                        else break;
                    } return null;
                }
                @Override protected void succeeded() {
                    super.succeeded();
                    SystemController.closeVideoHandlers();
                    System.out.println("Everything is done!");
                    ViewController.processIsComplete(pane2,label1);
                }
                @Override protected void cancelled() {
                    super.cancelled();
                    updateMessage("Thread has been cancelled!");
                }
                @Override protected void failed() {
                    super.failed();
                    updateMessage("Thread has failed!");
                }
            };
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
        }
    }

    public static VideoCapture getCap() {
        return cap;
    }
}
