package processors;

import entities.ApplicationPaths;
import entities.Controllers;
import entities.StructuringElement;
import controllers.MainController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.tesseract;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static org.bytedeco.javacpp.lept.pixRead;

/**
 * Created by arxa on 16/11/2016.
 */

public class VideoProcessor
{
    private static Mat canny = new Mat();
    private static Mat input = new Mat();
    private static Mat inputWithTextBlocks = new Mat();
    private static Mat src = new Mat();
    private static Mat dst = new Mat();
    private static Mat unsharp = new Mat();
    private static Thread thread;
    private static boolean extractUniqueWords;
    private static List<String> uniqueWords;
    private static boolean frameIsOpened;

    public static void processVideoFile(File videoFile)
    {
        VideoCapture cap = new VideoCapture(videoFile.getAbsolutePath());
        frameIsOpened = cap.read(input);

        VideoWriter videoWriter = new VideoWriter(Paths.get(ApplicationPaths.RESOURCES_OUTPUTS,
                ApplicationPaths.UNIQUE_FOLDER_NAME, "Video", "video.mp4").toAbsolutePath().toString(),
                VideoWriter.fourcc('X', '2','6','4'),
                cap.get(Videoio.CAP_PROP_FPS), new Size(cap.get(Videoio.CAP_PROP_FRAME_WIDTH), cap.get(Videoio.CAP_PROP_FRAME_HEIGHT)), true);

        if (!frameIsOpened){
            new Alert(Alert.AlertType.ERROR, "ERROR: Failed to read video frames\nTry another video file. If the error persists, contact the developer",
                    ButtonType.OK).showAndWait();
            return;
        }
        checkThreadStatus();
        Task<Void> task = new Task<Void>()
        {
            @Nullable
            @Override protected Void call() throws Exception
            {
                // OCR Initialization
                uniqueWords = new ArrayList<>();
                BytePointer ocrOutput = new BytePointer();
                tesseract.TessBaseAPI ocrApi = new tesseract.TessBaseAPI();
                OcrProcessor.initializeOcr(ocrApi);

                double frames = (int) cap.get(Videoio.CAP_PROP_FRAME_COUNT);
                int currentFrame = 1;
                input.copyTo(inputWithTextBlocks);
                Size kernel = StructuringElement.getStructuringElement(input.height()*input.width());
                Mat structuringElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, kernel);
                Platform.runLater(() -> {
                    Controllers.getLogController().logTextArea.appendText("[Structuring Element: "+kernel.height+" x "+kernel.width+"]\n");
                });
                while (frameIsOpened && !thread.isInterrupted())
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

                    // Apply the MaximumGradientDifference(MGD) operator
                    double[][] mgdArray = PixelProcessor.getMgdArray(dst);

                    // Convert the mgdArray back again into a Mat object
                    src = PixelProcessor.arrayToMat(mgdArray, dst.height(), dst.width(), CvType.CV_16S);
                    ImageWriter.writeStep(src);

                    // Convert to Binary
                    src.convertTo(src, CvType.CV_8UC1);
                    Imgproc.threshold(src, dst, 80,255,Imgproc.THRESH_BINARY);
//                        dst = ImageProcessor.thresholdImageWithKmeans(src);
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
                    Imgproc.Canny(input, canny,50, 150);
                    ImageWriter.writeStep(canny);

                    // Find the candidate text blocks by finding the connected components of dilated image
                    List<Rect> textBlocks = ImageProcessor.findTextBlocks(src);
                    ImageProcessor.paintTextBlocks(textBlocks, inputWithTextBlocks);
                    ImageWriter.writeStep(inputWithTextBlocks);
                    ImageWriter.writePaintedFrame(inputWithTextBlocks);

                    // Write painted frame to video
                    videoWriter.write(inputWithTextBlocks);

                    // Preprocess textblocks and extract text with OCR
                    if (Controllers.getSettingsController().extractTextCheckBox.isSelected()){
                        for (Mat textBlock : ImageProcessor.getCroppedTextBlocks(textBlocks)){
                            String imagePath = preprocessTextBlock(textBlock,ocrApi,ocrOutput);
                            applyOCR(ocrApi,ocrOutput,imagePath);
                        }
                    }

                        /*
                        Reading/Skipping the next 5 frames to speed up.
                        Human readable text should last more than 5 frame at least.
                         */
                    for (int i=0; i < 5; i++) {
                        cap.read(input);
                        currentFrame++;
                        input.copyTo(inputWithTextBlocks);
                        ImageProcessor.paintTextBlocks(textBlocks, inputWithTextBlocks);
                        // These frames are not processed, however we shall write them to the video result,
                        // using the last detected text areas
                        videoWriter.write(inputWithTextBlocks);
                    }
                    frameIsOpened = cap.read(input);
                    currentFrame++;
                    input.copyTo(inputWithTextBlocks);

                    // Update progress bar
                    final int currentFrame_final = currentFrame;
                    final double frames_final = frames;
                    Platform.runLater(() -> {
                        Controllers.getMainController().progressBar.setProgress(currentFrame_final/frames_final);
                    });
                }
                ocrApi.End();
                ocrOutput.deallocate();
                return null;
            }
            @Override protected void succeeded() {
                cap.release();
                videoWriter.release();
                Controllers.getLogController().logTextArea.appendText("Operation completed!\n");
                Controllers.getMainController().progressIndicator.setVisible(false);
                Controllers.getMainController().processButton.setVisible(true);
                Controllers.getMainController().progressBar.setProgress(0.0);
                Controllers.getMainController().progressBar.setVisible(false);
                Player.playProcessedVideo();
                super.succeeded();
            }
        };
        // Catching Thread Exceptions
        task.exceptionProperty().addListener((observable, oldValue, newValue) ->  {
            if(newValue != null) {
                try{
                    Controllers.getLogController().logTextArea.appendText("ERROR: Thread Exception: " + ExceptionUtils.getStackTrace(newValue)+"\n");
                } catch (ClassCastException e){
                    Controllers.getLogController().logTextArea.appendText("Could not cast Exception in Thread\n");
                }
                MainController.getLogStage().show();
            }
        });
        thread = new Thread(task);
        thread.setDaemon(false);
        thread.start();
    }



    /**
     *  Preprocesses the text blocks, before proceeding to OCR, in order
     *  to achieve better extraction results
     * @param textBlock List of image's text blocks in Rect format
     */
    private static String preprocessTextBlock(Mat textBlock, tesseract.TessBaseAPI api, BytePointer ocrOutput)
    {
        ImageWriter.writeOCRImage(textBlock);
        Imgproc.cvtColor(textBlock, src, Imgproc.COLOR_RGB2GRAY, 0);
        ImageWriter.writeOCRImage(src);

        Imgproc.GaussianBlur(src, dst, new Size(0, 0), 3);
        ImageWriter.writeOCRImage(dst);

        Core.addWeighted(src, 1.5, dst, -0.5, 0, unsharp);
        ImageWriter.writeOCRImage(unsharp);

        Core.normalize(unsharp, src,0.0,1.0, Core.NORM_MINMAX);
        Mat binary = ImageProcessor.thresholdImageWithKmeans(src);

        ImageWriter.writeOCRImage(binary);
        //Imgproc.resize(binary, binary, new Size(), 4.0, 4.0, Imgproc.INTER_LINEAR);
        return ImageWriter.writeTextBlock(binary).getAbsolutePath();
    }

    private static void applyOCR(tesseract.TessBaseAPI api, BytePointer ocrOutput, String filePath){
        lept.PIX image = pixRead(filePath);
        api.SetImage(image);
        ocrOutput = api.GetUTF8Text();
        if (ocrOutput == null) {
            Platform.runLater(() -> {
                Controllers.getLogController().logTextArea.appendText("OcrProcessor Text is NULL - Continuing forward\n");
            });
            MainController.getLogStage().show();
            return;
        }
        String[] words = ocrOutput.getString().trim().split(" ");
        for (String word : words){
            String cleaned = OcrProcessor.removeSpecialCharacters(word);
            String spelled = OcrProcessor.checkForSpelling(cleaned);
            if (extractUniqueWords) {
                if (uniqueWords.contains(spelled)){
                    continue;
                }
                uniqueWords.add(spelled);
                Platform.runLater(() -> {
                    Controllers.getMainController().textArea.appendText(spelled + " ");
                });
                continue;
            }
            Platform.runLater(() -> {
                Controllers.getMainController().textArea.appendText(spelled + " ");
            });
        }
    }

    @Contract(pure = true)
    public static Mat getCanny() {
        return canny;
    }

    @Contract(pure = true)
    public static Mat getInput() {
        return input;
    }

    public static void setExtractUniqueWords(boolean extractUniqueWords) {
        VideoProcessor.extractUniqueWords = extractUniqueWords;
    }

    public static void checkThreadStatus(){
        if (thread != null && thread.isAlive()){
            thread.interrupt();
        }
    }
}
