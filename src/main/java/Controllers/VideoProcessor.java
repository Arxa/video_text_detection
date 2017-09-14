package Controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

import java.io.File;
import java.util.List;

/**
 * Created by arxa on 16/11/2016.
 */

public class VideoProcessor
{
    private static Mat sobel = new Mat();
    private static Mat input = new Mat();
    private static Mat src = new Mat();
    private static Mat dst = new Mat();
    private static Mat unsharp = new Mat();

    public static void extractText(File videoFile)
    {
        VideoCapture cap = new VideoCapture(videoFile.getPath());
        VideoWriter videoWriter = new VideoWriter(ImageWriter.getUniquePath()+"\\Video\\video.mp4", VideoWriter.fourcc('x', '2','6','4'),
                cap.get(Videoio.CAP_PROP_FPS), new Size(cap.get(Videoio.CAP_PROP_FRAME_WIDTH), cap.get(Videoio.CAP_PROP_FRAME_HEIGHT)), true);

        if (cap.isOpened())
        {
            Task<Void> task = new Task<Void>()
            {
                @Override protected Void call() throws Exception
                {
                    boolean open1 = cap.read(input);
                    while (open1)
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

                            /*
                            Convert to GrayScale
                             */
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
                            Imgproc.Laplacian(src, dst, CvType.CV_16S,3,1,0);
                            ImageWriter.writeStep(dst);

                            /*
                            Convert to a 2D Array.
                            We need this conversion for the next step.
                             */
                            double[][] laplaceArray = PixelProcessor.matToArray(dst);

                            /*
                            Apply the MaximumGradientDifference(MGD) operator
                             */
                            double[][] mgdArray = PixelProcessor.find_MaximumGradientDifference(laplaceArray, dst.height(), dst.width());

                            /*
                            Convert the mgdArray back again into a Mat object
                             */
                            src = PixelProcessor.arrayToMat(mgdArray, dst.height(), dst.width(), CvType.CV_16S);
                            ImageWriter.writeStep(src);

                            /*
                            Apply [0..1] normalization
                             */
                            Core.normalize(src, dst,0.0,1.0, Core.NORM_MINMAX);

                            /*
                            Convert to Binary
                             */
                            dst.convertTo(dst, CvType.CV_8UC1);
                            Imgproc.threshold(dst, src,0, 255,Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
                            ImageWriter.writeStep(src);

                            /*
                            Apply the morphological operation 'Dilation'
                            ImgProc.dilate Parameters:
                            src – input image; the number of channels can be arbitrary,
                                but the depth should be one of CV_8U, CV_16U, CV_16S, CV_32F` or ``CV_64F.
                            dst – output image of the same size and type as src.
                            element – structuring element used for dilation;
                                if element=Mat() , a 3 x 3 rectangular structuring element is used.
                            anchor – position of the anchor within the element;
                                default value (-1, -1) means that the anchor is at the element center.
                             */
                            Imgproc.dilate(src, dst, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(13.0,13.0)));
                            ImageWriter.writeStep(dst);

                            /*
                            Calculate Sobel Edges of original image (i.e. input) - needed afterwards
                             */
                            Imgproc.Sobel(input, sobel, CvType.CV_8U,1,1);
                            ImageWriter.writeStep(sobel);

                            /*
                            Find text blocks by finding the connected components of
                            dilated image and filtering them based on their Sobel edges density.
                             */
                            List<Rect> textBlocks = MatProcessor.find_TextBlocks(dst);

                            /*
                            Paint the filtered textBlocks from above to the original frame (i.e. input)
                            */
                            MatProcessor.paintRectsToMat(textBlocks,input);
                            ImageWriter.writePaintedFrame(input);
                            ImageWriter.writeStep(input);

                            /*
                            Write painted frame to video
                             */
                            videoWriter.write(input);

                            /*
                            Extract Text from current frame's textblocks
                             */
                            extractText(textBlocks);

                            /*
                            Are any more frames to read?
                            Read frames with a step of 5 frames per time. No need to detect the same text
                             */
                            for (int i=0; i < 5; i++) {
                                cap.read(input);
                                MatProcessor.paintRectsToMat(textBlocks,input);
                                videoWriter.write(input);
                            }
                            open1 = cap.read(input);
                    }
                    return null;
                }
                @Override protected void succeeded() {
                    cap.release();
                    videoWriter.release();
                    References.getMainController().textArea.appendText("Operation completed!");
                    References.getMainController().progressIndicator.setProgress(1.0);
                    super.succeeded();
                }
                @Override protected void cancelled() {
                    videoWriter.release();
                    References.getMainController().textArea.appendText("ERROR: Something went wrong!");
                    super.cancelled();

                }
                @Override protected void failed() {
                    videoWriter.release();
                    References.getMainController().textArea.appendText("ERROR: Something went wrong!");
                    super.failed();
                }
            };
            // Catching Thread Exceptions
            task.exceptionProperty().addListener((observable, oldValue, newValue) ->  {
                if(newValue != null) {
                    Exception ex = (Exception) newValue;
                    References.getMainController().textArea.appendText(ex.getMessage());
                }
            });
            Thread th = new Thread(task);
            th.setDaemon(false);
            th.start();
        }
    }

    public static void extractText(List<Rect> textBlocks)
    {
        for (Mat m : MatProcessor.getTextBlocksAsMat(textBlocks))
        {
            /*
             Apply the 'Unsharp Mark' Filter
             */
            Imgproc.cvtColor(m, src, Imgproc.COLOR_RGB2GRAY, 0);
            Imgproc.GaussianBlur(src, dst, new Size(0, 0), 3);
            Core.addWeighted(src, 1.5, dst, -0.5, 0, unsharp);
            Core.normalize(unsharp, src,0.0,1.0, Core.NORM_MINMAX);
            src.convertTo(src, CvType.CV_8UC1);
            Imgproc.threshold(src, dst,0, 255,Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
            /*
             Extract Text
             */
            File f = ImageWriter.writeTextBlock(dst);
            String extracted_text = OCR.applyOCR(f.getPath());
            Platform.runLater(() -> {
                References.getMainController().textArea.appendText(extracted_text);
            });
        }
    }

    public static Mat getSobel() {
        return sobel;
    }

    public static Mat getInput() {
        return input;
    }
}
