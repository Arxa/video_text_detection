package Models;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arxa on 23/11/2016.
 */

public class FrameCorners
{
    private Mat frame;
    private List<Corner> cornersList;
    private List<Corner> stableCorners;
    private List<Corner> movingCorners;
    private List<Corner> qualifiedMovingCorners;

    public FrameCorners()
    {}

    public FrameCorners(Mat frame1, List<Corner> corners1)
    {
        frame = frame1;
        cornersList = corners1;
        stableCorners = new ArrayList<>();
        movingCorners = new ArrayList<>();
        qualifiedMovingCorners = new ArrayList<>();
    }

    public Mat getFrame() {
        return frame;
    }

    public List<Corner> getCornersList() {
        return cornersList;
    }

    public List<Corner> getStableCorners() {
        return stableCorners;
    }

    public List<Corner> getMovingCorners() {
        return movingCorners;
    }

    public List<Corner> getQualifiedMovingCorners() {
        return qualifiedMovingCorners;
    }

}
