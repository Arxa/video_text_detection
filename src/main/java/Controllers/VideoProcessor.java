package Controllers;

import static Models.ImageContainer.*;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_COUNT;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.control.ProgressBar;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.util.List;


/**
 * Created by arxa on 16/11/2016.
 */

public class VideoProcessor
{
    private static VideoCapture cap;

    /**
     * @author Nikiforos Archakis
     */

    public static void processVideo(ProgressBar progressBar1, TextArea logArea,
                                    Button chooseButton, ProgressIndicator progressIndicator1,
                                    TextArea extractedText_Area)
    {
        progressBar1.setProgress(0.0);
        progressIndicator1.setProgress(0.0);
        /*
        Required in order to use core OpenCV methods
         */
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        cap = new VideoCapture(Player.getFilename().toString());
        if (cap.isOpened())
        {
            Task<Void> task = new Task<Void>()
            {
                @Override protected Void call() throws Exception
                {
                    init(); // Using Static import of ImageContainer here and in code below
                    boolean open1 = cap.read(getInput());
                    Double progress = 0.0;
                    int frameIterations = 0;
                    double percentageProgress = 0.1;
                    int totalFPS = (int) cap.get(CAP_PROP_FRAME_COUNT);
                    Platform.runLater(() -> {
                        progressIndicator1.setProgress(-1.0); // Waiting mode
                    });

                    while (true)
                    {
                        if (open1)
                        {
                            /*
                            Initialize a Mat object for the Gaussian Blurred image
                             */
                            setInput_GB(new Mat(getInput().size(),getInput().type()));

                            /*
                            (Apply Gaussian Blurred Filter)
                            Input -> Gaussian Blurred Filter = input_GB

                            GaussianBlur Parameters:

                            src – input image
                            dst – output image of the same size and type as src.
                            ksize – Gaussian kernel size. ksize.width and ksize.height
                                can differ but they both must be positive and odd.
                                Or, they can be zero’s and then they are computed from sigma* .
                            sigmaX – Gaussian kernel standard deviation in X direction.
                            sigmaY – Gaussian kernel standard deviation in Y direction;
                                if sigmaY is zero, it is set to be equal to sigmaX,
                                if both sigmas are zeros, they are computed from ksize.width and ksize.height
                             */
                            Imgproc.GaussianBlur(getInput(),getInput_GB(),
                                    new Size(15.0,15.0),0.0,0.0);

                            /*
                            (Change color space to GrayScale)
                            input_GB -> GrayScale = input_GB_Gray
                             */
                            Imgproc.cvtColor(getInput_GB(), getInput_GB_Gray(), Imgproc.COLOR_RGB2GRAY, 0);

                            /*
                            (Apply the Laplacian Filter)
                            input_GB_Gray -> Laplacian Filter = input_GB_Gray_LPL

                            Laplacian Parameters:

                            src – Source image.
                            dst – Destination image of the same size and the same number of channels as src .
                            ddepth – Desired depth of the destination image.
                            ksize – Aperture size used to compute the second-derivative filters.
                                The ksize must be positive and odd. Bigger ksize leads to stronger intensity.
                            scale – Optional scale factor for the computed Laplacian values.
                                By default, no scaling is applied.
                            delta – Optional delta value that is added to the results prior to storing them in dst.
                             */
                            Imgproc.Laplacian(getInput_GB_Gray(),getInput_GB_Gray_LPL(),
                                    CvType.CV_16S,1,1,0);

                            /*
                            Convert the input_GB_Gray_LPL Mat object to a 2D Array.
                            (Wee need this conversion for the next step).
                            input_GB_Gray_LPL -> toArray = input_GB_Gray_LPL_in_Array
                             */
                            setInput_GB_Gray_LPL_in_Array
                                    (PixelProcessor.matToArray(getInput_GB_Gray_LPL()));

                            /*
                            Apply the MaximumGradientDifference(MGD) operator to input_GB_Gray_LPL_in_Array
                            and store it to input_GB_Gray_LPL_MGD_in_Array
                             */
                            setInput_GB_Gray_LPL_MGD_in_Array(PixelProcessor.
                                    find_MaximumGradientDifference(getInput_GB_Gray_LPL_in_Array(),
                                            getInput_GB_Gray_LPL().height(),
                                                    getInput_GB_Gray_LPL().width()));

                            /*
                            Convert the input_GB_Gray_LPL_MGD_in_Array back again into a Mat object
                            and store it into input_GB_Gray_LPL_MGD
                             */
                            setInput_GB_Gray_LPL_MGD(PixelProcessor.
                                    arrayToMat(getInput_GB_Gray_LPL_MGD_in_Array(),
                                            getInput_GB_Gray_LPL().height(),getInput_GB_Gray_LPL().width(),CvType.CV_16S));

                            /*
                            Initialize Mat file to store the Normalization result
                             */
                            getInput_GB_Gray_LPL_MGD_NORM().create
                                    (getInput_GB_Gray_LPL_MGD().size(),CvType.CV_32FC1);

                            /*
                            Apply [0..1] normalization to input_GB_Gray_LPL_MGD and
                            store it into input_GB_Gray_LPL_MGD_NORM
                             */
                            Core.normalize(getInput_GB_Gray_LPL_MGD(),
                                    getInput_GB_Gray_LPL_MGD_NORM(),0.0,1.0,
                                            Core.NORM_MINMAX, getInput_GB_Gray_LPL_MGD_NORM().type());

                            /*
                            Apply K-Means Clustering to input_GB_Gray_LPL_MGD_NORM with
                            cluster values of 0 and 1. Store the result into input_GB_Gray_LPL_MGD_NORM_KMEANS
                            (See Clustering.k_means for details)
                             */
                            setInput_GB_Gray_LPL_MGD_NORM_KMEANS
                                    (Clustering.k_Means(getInput_GB_Gray_LPL_MGD_NORM()));

                            /*
                            Initializing 'Binary' image filled with zeros (black)
                            named input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN
                             */
                            setInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN
                                    (new Mat(getInput_GB_Gray_LPL_MGD_NORM().size(),
                                            CvType.CV_8UC1,new Scalar(0.0)));

                            /*
                            Fill the K-MEANS 'text' cluster result (i.e. 1) values to binary image above
                            as ones (whites) and store the image to input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN
                             */
                            Visualizer.paintMatToBinary(getInput_GB_Gray_LPL_MGD_NORM_KMEANS(),
                                                    getInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN());

                            /*
                            Initialize image for the Dilation operation named
                            input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED
                             */
                            setInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED(
                                    new Mat(getInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN().rows(),
                                            getInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN().cols(),
                                                    getInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN().type()));

                            /*
                            Apply the morphological operation 'Dilation' to
                            input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN and store the result into
                            input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED

                            ImgProc.dilate Parameters

                            src – input image; the number of channels can be arbitrary,
                                but the depth should be one of CV_8U, CV_16U, CV_16S, CV_32F` or ``CV_64F.
                            dst – output image of the same size and type as src.
                            element – structuring element used for dilation;
                                if element=Mat() , a 3 x 3 rectangular structuring element is used.
                            anchor – position of the anchor within the element;
                                default value (-1, -1) means that the anchor is at the element center.
                             */
                            Imgproc.dilate(getInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN(),
                                    getInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED(),
                                        Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                                                new Size(13.0,13.0)));

                            /*
                            Calculate Sobel Edges of original image (i.e. input) - needed below
                             */
                            Imgproc.Sobel(getInput(),getInput_Sobel(),CvType.CV_8U,1,1);

                            /*
                            Find text blocks by finding the connected components of
                            input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED and filtering them based
                            on their Sobel edges density. See Clustering.find_TextBlocks for more details.
                             */
                            List<Rect> textBlocks = Clustering.find_TextBlocks
                                    (getInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED());

                            /*
                            Paint the filtered textBlocks from above to the original frame (i.e. input)
                            */
                            getInput().copyTo(getInput_Painted());
                            Visualizer.paintRectsToMat(textBlocks,getInput_Painted());

                            Writer.writePaintedFrame(getInput_Painted());

                            for (Mat m : MatProcessor.getTextBlockList(textBlocks))
                            {
                                /*
                                Applying the 'Unsharp Mark' Filter
                                 */
                                Mat gray = new Mat();
                                Imgproc.cvtColor(m, gray, Imgproc.COLOR_RGB2GRAY, 0);
                                Mat gauss = new Mat();
                                Mat final_image = new Mat();
                                Imgproc.GaussianBlur(gray, gauss, new Size(0, 0), 3);
                                Core.addWeighted(gray, 1.5, gauss, -0.5, 0, final_image);

                                Mat norm = new Mat(final_image.size(), CvType.CV_32FC1);

                                Core.normalize(final_image, norm,0.0,1.0,
                                        Core.NORM_MINMAX, norm.type());
                                Mat kmeans = Clustering.k_Means(norm);
                                Mat bin = new Mat(norm.size(), CvType.CV_8UC1,new Scalar(0.0));
                                Visualizer.paintMatToBinary(kmeans, bin);
                                File f = Writer.writeTextBlock(bin);
                                String extracted_text = OCR.applyOCR(f.getPath());

                                Platform.runLater(() -> {
                                    extractedText_Area.setText(extractedText_Area.getText() + extracted_text + "\n");
                                });
                            }

                            /*
                            Progress Bar code
                             */
                            if (++frameIterations > (int)(totalFPS * percentageProgress))
                            {
                                progress += 0.1;
                                percentageProgress += 0.1;
                            }

                            final double f_progress = progress;

                            if (Double.compare(f_progress, 1.0) < 0) {
                                Platform.runLater(() -> {
                                    progressBar1.setProgress(f_progress);
                                });
                            }

                            /*
                            Initializing all image objects
                             */
                            init();

                            /*
                            Are any more frames to read?
                            Also, read frames with a step of 5 frames per time.
                            No need to detect the same text
                             */
                            for (int i=0; i < 5; i++) {
                                open1 = cap.read(getInput());
                                frameIterations++;
                            }

                        }
                        else break;
                    }
                    return null;
                }
                @Override protected void succeeded() {
                    super.succeeded();
                    progressBar1.setProgress(1.0);
                    progressIndicator1.setProgress(1.0);
                    SystemController.closeVideoHandlers();
                    Log.printLogMessageToGUI(logArea,"Video processing has successfully completed!");
                    chooseButton.setDisable(false);
                    System.out.println("ok");
                }
                @Override protected void cancelled() {
                    super.cancelled();
                    progressBar1.setProgress(0.0);
                    progressIndicator1.setProgress(0.0);
                    Log.printLogMessageToGUI(logArea,"Video processing has been canceled!");
                    chooseButton.setDisable(false);
                }
                @Override protected void failed() {
                    super.failed();
                    progressBar1.setProgress(0.0);
                    progressIndicator1.setProgress(0.0);
                    Log.printLogMessageToGUI(logArea,"Video processing has failed!");
                    chooseButton.setDisable(false);
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
