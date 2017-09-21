package Controllers;

import Models.StructuringElement;
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
        VideoWriter videoWriter = new VideoWriter(ImageWriter.getUniquePath()+"\\Video\\video.mp4", VideoWriter.fourcc('H', '2','6','4'),
                cap.get(Videoio.CAP_PROP_FPS), new Size(cap.get(Videoio.CAP_PROP_FRAME_WIDTH), cap.get(Videoio.CAP_PROP_FRAME_HEIGHT)), true);

        if (cap.isOpened())
        {
            Task<Void> task = new Task<Void>()
            {
                @Override protected Void call() throws Exception
                {
                    boolean open1 = cap.read(input);
                    Size kernel = StructuringElement.getStructuringElement(input.height()*input.width());
                    Mat structuringElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, kernel);
                    References.getMainController().textArea.appendText("S.E selected: "+kernel.height+"-"+kernel.width);
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
                            Imgproc.Laplacian(src, dst, CvType.CV_16S,3,2,0);
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
                           // Core.normalize(src, dst,0.0,1.0, Core.NORM_MINMAX);

                            /*
                            Convert to Binary
                             */
                            src.convertTo(src, CvType.CV_8UC1);
                            Imgproc.threshold(src, dst, 80,255,Imgproc.THRESH_BINARY);
                            ImageWriter.writeStep(dst);

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
                            Imgproc.dilate(dst, src, structuringElement);
                            ImageWriter.writeStep(src);

                            /*
                            Calculate Sobel Edges of original image (i.e. input) - needed afterwards
                             */
                            Imgproc.Canny(input, sobel,50, 150);
//                            Mat withLines = new Mat();
//                            Mat lines = new Mat();
                            //sobel.copyTo(withLines);

                        // Imgproc.cvtColor(sobel, withLines, Imgproc.COLOR_BGR2GRAY);

//                        Imgproc.HoughLinesP(sobel, lines, 1, Math.PI / 180, 150, 20, 50);
//
//                            for(int i = 0; i < lines.rows(); i++) {
//                                double[] val = lines.get(i, 0);
//                                Imgproc.line(withLines, new Point(val[0], val[1]), new Point(val[2], val[3]), new Scalar(255), 1);
//                            }
//                        ImageWriter.writeStep(withLines);


                        ImageWriter.writeStep(sobel);

                            /*
                            Find text blocks by finding the connected components of
                            dilated image and filtering them based on their Sobel edges density.
                             */
                            List<Rect> textBlocks = MatProcessor.find_TextBlocks(src);

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
            ImageWriter.writeOCRImage(m);
            Imgproc.cvtColor(m, src, Imgproc.COLOR_RGB2GRAY, 0);
            ImageWriter.writeOCRImage(src);


            Imgproc.GaussianBlur(src, dst, new Size(0, 0), 3);
            ImageWriter.writeOCRImage(dst);

            Core.addWeighted(src, 1.5, dst, -0.5, 0, unsharp);
            ImageWriter.writeOCRImage(unsharp);

            //Core.normalize(unsharp, src,0.0,1.0, Core.NORM_MINMAX);
            //src.convertTo(src, CvType.CV_8UC1);
            //Imgproc.threshold(unsharp, src, 80,255,Imgproc.THRESH_BINARY);
            //Imgproc.adaptiveThreshold(unsharp,src,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);
            Core.normalize(unsharp, src,0.0,1.0, Core.NORM_MINMAX);
            Mat kmeans = MatProcessor.k_Means(src);
            Mat binary = new Mat(unsharp.height(), unsharp.width(), CvType.CV_8UC1);
            MatProcessor.paintMatToBinary(kmeans,binary);






            ImageWriter.writeOCRImage(binary);
//            Imgproc.resize(src, dst, new Size(), 4.0, 4.0, Imgproc.INTER_LINEAR);
//            ImageWriter.writeOCRImage(dst);


//            ImageInfo imageInfo = Sanselan.getImageInfo(file_);
//
//            int physicalWidthDpi = imageInfo.getPhysicalWidthDpi();
//            int physicalHeightDpi = imageInfo.getPhysicalHeightDpi();

            //ImageWriter.writeStep(src);
            /*
             Extract Text
             */
            File f = ImageWriter.writeTextBlock(binary);
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
