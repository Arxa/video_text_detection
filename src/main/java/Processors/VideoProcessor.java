package Processors;

import Entities.Controllers;
import Entities.StructuringElement;
import ViewControllers.MainController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by arxa on 16/11/2016.
 */

public class VideoProcessor
{
    private static Mat sobel = new Mat();
    private static Mat input = new Mat();
    private static Mat inputPainted = new Mat();
    private static Mat src = new Mat();
    private static Mat dst = new Mat();
    private static Mat unsharp = new Mat();
    private static Thread thread;

    public static void processVideoFile(File videoFile)
    {
        OcrProcessor.initUniqueWords();
        VideoCapture cap = new VideoCapture(videoFile.getPath());
        VideoWriter videoWriter = new VideoWriter(ImageWriter.getUniquePath()+"\\Video\\video.mp4", VideoWriter.fourcc('H', '2','6','4'),
                cap.get(Videoio.CAP_PROP_FPS), new Size(cap.get(Videoio.CAP_PROP_FRAME_WIDTH), cap.get(Videoio.CAP_PROP_FRAME_HEIGHT)), true);

        if (cap.isOpened())
        {
            Task<Void> task = new Task<Void>()
            {
                @Override protected Void call() throws Exception
                {
                    double frames = (int) cap.get(Videoio.CAP_PROP_FRAME_COUNT);
                    boolean open1 = cap.read(input);
                    int currentFrame = 1;
                    input.copyTo(inputPainted);
                    Size kernel = StructuringElement.getStructuringElement(input.height()*input.width());
                    Mat structuringElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, kernel);
                    Controllers.getLogController().logTextArea.appendText("[Structuring Element: "+kernel.height+" x "+kernel.width+"]\n");
                    while (open1 && !thread.isInterrupted())
                    {
                        ImageWriter.writeStep(input);

                        /*
                        Apply Gaussian Blurred Filter
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
                        Imgproc.GaussianBlur(input, dst, new Size(15.0,15.0),0.0,0.0);
                        ImageWriter.writeStep(dst);

                        // Convert to GrayScale
                        Imgproc.cvtColor(dst, src, Imgproc.COLOR_RGB2GRAY, 0);
                        ImageWriter.writeStep(src);

                        /*
                        Apply the Laplacian Filter
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
                        Imgproc.Laplacian(src, dst, CvType.CV_16S,3,2,0);
                        ImageWriter.writeStep(dst);


                        // Convert to a 2D Array. We need this conversion for the next step.
                        double[][] laplaceArray = PixelProcessor.matToArray(dst);


                        // Apply the MaximumGradientDifference(MGD) operator
                        double[][] mgdArray = PixelProcessor.find_MaximumGradientDifference(laplaceArray, dst.height(), dst.width());


                        // Convert the mgdArray back again into a Mat object
                        src = PixelProcessor.arrayToMat(mgdArray, dst.height(), dst.width(), CvType.CV_16S);
                        ImageWriter.writeStep(src);

                        // Convert to Binary
                        src.convertTo(src, CvType.CV_8UC1);
                        Imgproc.threshold(src, dst, 80,255,Imgproc.THRESH_BINARY);
                        ImageWriter.writeStep(dst);

                        /*
                        Apply the morphological operation Dilation
                        ImgProc.dilate Parameters:
                        src – input image; the number of channels can be arbitrary,
                              but the depth should be one of CV_8U, CV_16U, CV_16S, CV_32F` or ``CV_64F.
                        dst – output image of the same size and type as src.
                        element – structuring element used for dilation;
                              if element=Mat() , a 3 x 3 rectangular structuring element is used.
                        anchor – position of the anchor within the element;
                              default value (-1, -1) means that the anchor is at the element center.
                         */
                        Imgproc.dilate(dst, src, structuringElement);
                        ImageWriter.writeStep(src);

                        // Calculate Canny Edges of original frame
                        Imgproc.Canny(input, sobel,50, 150);
                        ImageWriter.writeStep(sobel);

                        /*
                        Find text blocks by finding the connected components of
                        dilated image and filtering them based on their Sobel edges density.
                         */
                        List<Rect> textBlocks = MatProcessor.find_TextBlocks(src);

                        // Paint the filtered textBlocks from above to the original frame (i.e. input)
                        MatProcessor.paintRectsToMat(textBlocks,inputPainted);
                        ImageWriter.writePaintedFrame(inputPainted);
                        ImageWriter.writeStep(inputPainted);

                        // Write painted frame to video
                        videoWriter.write(inputPainted);

                        // Extract Text from current frame's textblocks
                        preprocessTextBlocks(textBlocks);

                        /*
                        Reading/Skipping the next 5 frames to speed up.
                        Human readable text should last more than 5 frame at least.
                         */
                        for (int i=0; i < 5; i++) {
                            cap.read(input);
                            currentFrame++;
                            input.copyTo(inputPainted);
                            MatProcessor.paintRectsToMat(textBlocks,inputPainted);
                            // These frames are not processed, however we shall write them to the video result, using the last detected text areas
                            videoWriter.write(inputPainted);
                        }
                        open1 = cap.read(input);
                        currentFrame++;
                        input.copyTo(inputPainted);

                        // Update progress bar
                        final int currentFrame_final = currentFrame;
                        final double frames_final = frames;
                        Platform.runLater(() -> {
                            Controllers.getMainController().progressBar.setProgress(currentFrame_final/frames_final);
                        });
                    }
                    return null;
                }
                @Override protected void succeeded() {
                    cap.release();
                    videoWriter.release();
                    // TODO do smth about the following copied code?
                    Controllers.getLogController().logTextArea.appendText("Operation completed!\n");
                    Controllers.getMainController().progressIndicator.setVisible(false);
                    Controllers.getMainController().extractButton.setVisible(true);
                    Controllers.getMainController().progressBar.setProgress(0.0);
                    Controllers.getMainController().progressBar.setVisible(false);
                    super.succeeded();
                }
                @Override protected void cancelled() {
                    videoWriter.release();
                    Controllers.getLogController().logTextArea.appendText("ERROR: Thread Cancelled!\n");
                    MainController.getLogStage().show();
                    Controllers.getMainController().progressIndicator.setVisible(false);
                    Controllers.getMainController().extractButton.setVisible(true);
                    Controllers.getMainController().progressBar.setProgress(0.0);
                    Controllers.getMainController().progressBar.setVisible(false);
                    super.cancelled();
                }
                @Override protected void failed() {
                    videoWriter.release();
                    Controllers.getLogController().logTextArea.appendText("ERROR: Thread Failed!\n");
                    MainController.getLogStage().show();
                    Controllers.getMainController().progressIndicator.setVisible(false);
                    Controllers.getMainController().extractButton.setVisible(true);
                    Controllers.getMainController().progressBar.setProgress(0.0);
                    Controllers.getMainController().progressBar.setVisible(false);
                    super.failed();
                }
            };

            // Catching Thread Exceptions
            task.exceptionProperty().addListener((observable, oldValue, newValue) ->  {
                if(newValue != null) {
                    Exception ex = (Exception) newValue;
                    Controllers.getLogController().logTextArea.appendText("ERROR: Thread Exception: " + ex.getMessage()+"\n");
                    MainController.getLogStage().show();
                }
            });

            thread = new Thread(task);
            thread.setDaemon(false);
            thread.start();
        }
    }

    /**
     *
     * @param textBlocks
     */
    public static void preprocessTextBlocks(List<Rect> textBlocks)
    {
        for (Mat m : MatProcessor.getTextBlocksAsMat(textBlocks))
        {
            ImageWriter.writeOCRImage(m);
            Imgproc.cvtColor(m, src, Imgproc.COLOR_RGB2GRAY, 0);
            ImageWriter.writeOCRImage(src);

            Imgproc.GaussianBlur(src, dst, new Size(0, 0), 3);
            ImageWriter.writeOCRImage(dst);

            Core.addWeighted(src, 1.5, dst, -0.5, 0, unsharp);
            ImageWriter.writeOCRImage(unsharp);

            Core.normalize(unsharp, src,0.0,1.0, Core.NORM_MINMAX);
            Mat kmeans = MatProcessor.k_Means(src);
            Mat binary = new Mat(unsharp.height(), unsharp.width(), CvType.CV_8UC1);
            MatProcessor.paintMatToBinary(kmeans,binary);

            ImageWriter.writeOCRImage(binary);
            //Imgproc.resize(binary, binary, new Size(), 4.0, 4.0, Imgproc.INTER_LINEAR);

            // Extract Text
            File f = ImageWriter.writeTextBlock(binary);
            String extracted_text = "";
            try {
                extracted_text = OcrProcessor.getOcrText(f.getPath());
            } catch (IOException e) {
                Controllers.getLogController().logTextArea.appendText("ERROR: OCR Exception: " + e.getMessage());
            }
            final String ocr_result = extracted_text;
            Platform.runLater(() -> {
                Controllers.getMainController().textArea.appendText(ocr_result);
            });
        }
    }

    public static Mat getSobel() {
        return sobel;
    }

    public static Mat getInput() {
        return input;
    }

    public static Thread getThread() {
        return thread;
    }
}
