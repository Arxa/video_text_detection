package Models;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arxa on 12/3/2017.
 */

public class ImageContainer
{
    private static Mat input;
    private static Mat input_Sobel;
    private static Mat input_Gray;
    private static Mat input_GB; // Gaussian Blurred
    private static Mat input_GB_Gray;
    private static Mat input_GB_Gray_LPL; // Laplacian
    private static Mat input_GB_Gray_LPL_MGD; // Maximum Gradient Difference
    private static Mat input_GB_Gray_LPL_MGD_NORM; // Normalized [0,1]
    private static Mat input_GB_Gray_LPL_MGD_NORM_KMEANS; // Clustering: 0 or 1
    private static Mat input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN; // BIN = 'Binary' 0 or 255 (Fake Binary)
    private static Mat input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED; // Dilation
    private static Mat input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED_FILTERED; // Filtered Dilated image: The approved Text Blocks

    private static double[][] input_GB_Gray_LPL_in_Array;
    private static double[][] input_GB_Gray_LPL_MGD_in_Array;

    private static List<Region> croppedTextRegions;



    public static void init()
    {
        input = new Mat();
        input_Sobel = new Mat();
        input_Gray = new Mat();
        input_GB = new Mat();
        input_GB_Gray = new Mat();
        input_GB_Gray_LPL = new Mat();
        input_GB_Gray_LPL_MGD = new Mat();
        input_GB_Gray_LPL_MGD_NORM = new Mat();
        input_GB_Gray_LPL_MGD_NORM_KMEANS = new Mat();
        input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN = new Mat();
        input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED = new Mat();
        input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED_FILTERED = new Mat();

        croppedTextRegions = new ArrayList<>();
    }

    public static Mat getInput() {
        return input;
    }

    public static void setInput(Mat input) {
        ImageContainer.input = input;
    }

    public static Mat getInput_Gray() {return input_Gray;
    }
    public static void setInput_Gray(Mat input_Gray) {ImageContainer.input_Gray = input_Gray;
    }

    public static Mat getInput_GB() {
        return input_GB;
    }

    public static void setInput_GB(Mat input_GB) {
        ImageContainer.input_GB = input_GB;
    }

    public static Mat getInput_GB_Gray() {
        return input_GB_Gray;
    }

    public static void setInput_GB_Gray(Mat input_GB_Gray) {
        ImageContainer.input_GB_Gray = input_GB_Gray;
    }

    public static Mat getInput_GB_Gray_LPL() {
        return input_GB_Gray_LPL;
    }

    public static void setInput_GB_Gray_LPL(Mat input_GB_Gray_LPL) {
        ImageContainer.input_GB_Gray_LPL = input_GB_Gray_LPL;
    }

    public static Mat getInput_GB_Gray_LPL_MGD() {
        return input_GB_Gray_LPL_MGD;
    }

    public static void setInput_GB_Gray_LPL_MGD(Mat input_GB_Gray_LPL_MGD) {
        ImageContainer.input_GB_Gray_LPL_MGD = input_GB_Gray_LPL_MGD;
    }

    public static Mat getInput_GB_Gray_LPL_MGD_NORM() {
        return input_GB_Gray_LPL_MGD_NORM;
    }

    public static void setInput_GB_Gray_LPL_MGD_NORM(Mat input_GB_Gray_LPL_MGD_NORM) {
        ImageContainer.input_GB_Gray_LPL_MGD_NORM = input_GB_Gray_LPL_MGD_NORM;
    }

    public static Mat getInput_GB_Gray_LPL_MGD_NORM_KMEANS() {
        return input_GB_Gray_LPL_MGD_NORM_KMEANS;
    }

    public static void setInput_GB_Gray_LPL_MGD_NORM_KMEANS(Mat input_GB_Gray_LPL_MGD_NORM_KMEANS) {
        ImageContainer.input_GB_Gray_LPL_MGD_NORM_KMEANS = input_GB_Gray_LPL_MGD_NORM_KMEANS;
    }

    public static double[][] getInput_GB_Gray_LPL_in_Array() {
        return input_GB_Gray_LPL_in_Array;
    }

    public static void setInput_GB_Gray_LPL_in_Array(double[][] input_GB_Gray_LPL_in_Array) {
        ImageContainer.input_GB_Gray_LPL_in_Array = input_GB_Gray_LPL_in_Array;
    }

    public static double[][] getInput_GB_Gray_LPL_MGD_in_Array() {
        return input_GB_Gray_LPL_MGD_in_Array;
    }

    public static void setInput_GB_Gray_LPL_MGD_in_Array(double[][] input_GB_Gray_LPL_MGD_in_Array) {
        ImageContainer.input_GB_Gray_LPL_MGD_in_Array = input_GB_Gray_LPL_MGD_in_Array;
    }

    public static Mat getInput_Sobel() {
        return input_Sobel;
    }

    public static void setInput_Sobel(Mat input_Sobel) {
        ImageContainer.input_Sobel = input_Sobel;
    }

    public static Mat getInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN() {
        return input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN;
    }

    public static void setInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN(Mat input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN) {
        ImageContainer.input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN = input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN;
    }

    public static Mat getInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED() {
        return input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED;
    }

    public static void setInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED(Mat input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED) {
        ImageContainer.input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED = input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED;
    }

    public static Mat getInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED_FILTERED() {
        return input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED_FILTERED;
    }

    public static void setInput_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED_FILTERED(Mat input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED_FILTERED) {
        ImageContainer.input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED_FILTERED = input_GB_Gray_LPL_MGD_NORM_KMEANS_BIN_DILATED_FILTERED;
    }

    public static List<Region> getCroppedTextRegions() {
        return croppedTextRegions;
    }

    public static void setCroppedTextRegions(List<Region> croppedTextRegions) {
        ImageContainer.croppedTextRegions = croppedTextRegions;
    }
}
