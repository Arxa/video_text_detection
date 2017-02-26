package Models;

import org.opencv.core.Mat;

/**
 * Created by arxa on 11/12/2016.
 */

public class Frame
{
    private Mat frame;

    public Frame(Mat mat1) {
        frame = mat1;
    }

    public Mat getFrame() {
        return frame;
    }

    public void setFrame(Mat frame) {
        this.frame = frame;
    }
}
