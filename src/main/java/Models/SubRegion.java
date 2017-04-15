package Models;

import org.opencv.core.Mat;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by arxa on 30/3/2017.
 */

public class SubRegion
{
    private Mat matSubregion;
    private Map<Integer,Integer> lbp_histogram;

    public SubRegion()
    {
        matSubregion = new Mat();
        lbp_histogram = new HashMap<>();
    }

    public SubRegion(Mat m)
    {
        matSubregion = new Mat();
        m.copyTo(matSubregion);
        lbp_histogram = new HashMap<>();
    }

    public Mat getMatSubregion() {
        return matSubregion;
    }

    public void setMatSubregion(Mat matSubregion) {
        this.matSubregion = matSubregion;
    }

    public Map<Integer, Integer> getLbp_histogram() {
        return lbp_histogram;
    }

    public void setLbp_histogram(Map<Integer, Integer> lbp_histogram) {
        this.lbp_histogram = lbp_histogram;
    }
}
