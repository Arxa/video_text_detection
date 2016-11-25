package Models;

import org.opencv.core.Mat;
import java.util.List;

/**
 * Created by arxa on 23/11/2016.
 */

public class FrameCorners
{
    private Mat frame;
    private List<Corner> cornersList;

    public FrameCorners()
    {}

    public FrameCorners(Mat frame1, List<Corner> corners1)
    {
        frame = frame1;
        cornersList = corners1;
    }

    public Mat getFrame() {
        return frame;
    }

    public List<Corner> getCornersList() {
        return cornersList;
    }
}
